package com.example.creationdonnee

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.EditText
import android.widget.GridLayout
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import java.util.*

class InscriptionFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dbHelper = DatabaseHelper(requireContext())

        dbHelper.deleteAllUsers()

        dbHelper.addFakeUser()
        val callback = activity?.onBackPressedDispatcher?.addCallback(this) {
            activity?.supportFragmentManager?.popBackStack()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inscription, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rootView = requireView()

        val submitButton = rootView.findViewById<Button>(R.id.soumettre_button)
        val hobbiesGrid = rootView.findViewById<GridLayout>(R.id.hobbiesGridLayout)

        submitButton.setOnClickListener {
            val email = rootView.findViewById<EditText>(R.id.mailValue).text.toString()
            val username = rootView.findViewById<EditText>(R.id.usernameValue).text.toString()
            val nameVal = rootView.findViewById<EditText>(R.id.nameValue).text.toString()
            val phoneNumber = rootView.findViewById<EditText>(R.id.phoneValue).text.toString()
            val dateOfBirth = rootView.findViewById<DatePicker>(R.id.dateOfBirthValue)
            val password = rootView.findViewById<EditText>(R.id.password).text.toString()
            var isValid = true

            resetErrors(rootView)

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                val mailEditText = rootView.findViewById<EditText>(R.id.mailValue)
                mailEditText.setText("")  // Effacer le texte du champ
                mailEditText.hint = "Email invalide"
                mailEditText.setHintTextColor(resources.getColor(android.R.color.holo_red_dark))
                isValid = false
            }

            if (username.length > 10 || username.contains(" ")) {
                val usernameEditText = rootView.findViewById<EditText>(R.id.usernameValue)
                usernameEditText.setText("")  // Effacer le texte du champ
                usernameEditText.hint = "Nom d'utilisateur invalide (max 10 caractères, pas d'espace)"
                usernameEditText.setHintTextColor(resources.getColor(android.R.color.holo_red_dark))
                isValid = false
            }

            val dbHelper = DatabaseHelper(requireContext())
            if (dbHelper.checkIfUserExists(email, username)) {
                val mailEditText = rootView.findViewById<EditText>(R.id.mailValue)
                val usernameEditText = rootView.findViewById<EditText>(R.id.usernameValue)
                mailEditText.setText("")
                usernameEditText.setText("")
                mailEditText.hint = "Email déjà utilisé"
                usernameEditText.hint = "Nom d'utilisateur déjà utilisé"
                mailEditText.setHintTextColor(resources.getColor(android.R.color.holo_red_dark))
                usernameEditText.setHintTextColor(resources.getColor(android.R.color.holo_red_dark))
                isValid = false
            }

            // Validation du numéro de téléphone
            if (!phoneNumber.matches("^\\+?[0-9]{10,15}\$".toRegex())) {
                val phoneEditText = rootView.findViewById<EditText>(R.id.phoneValue)
                phoneEditText.setText("")
                phoneEditText.hint = "Numéro de téléphone invalide"
                phoneEditText.setHintTextColor(resources.getColor(android.R.color.holo_red_dark))
                isValid = false
            }

            if (isValid) {
                val hobbies = mutableListOf<String>()
                for (i in 0 until hobbiesGrid.childCount) {
                    val child = hobbiesGrid.getChildAt(i)
                    if (child is CheckBox && child.isChecked) {
                        hobbies.add(child.text.toString())
                    }
                }

                val bundle = Bundle()
                bundle.putString("email", email)
                bundle.putString("username", username)
                bundle.putString("name", nameVal)
                bundle.putString("phoneNumber", phoneNumber)
                bundle.putString("password", password)
                bundle.putSerializable("hobbies", ArrayList(hobbies))

                val calendar = Calendar.getInstance()
                calendar.set(dateOfBirth.year, dateOfBirth.month, dateOfBirth.dayOfMonth)
                val dateOfBirthInMillis = calendar.timeInMillis
                bundle.putLong("dateOfBirth", dateOfBirthInMillis)

                val summaryFragment = SummaryFragment()
                summaryFragment.arguments = bundle

                val fragmentTransaction: FragmentTransaction = activity?.supportFragmentManager?.beginTransaction()!!
                fragmentTransaction.replace(R.id.fragment_container_main, summaryFragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
        }
    }

    private fun resetErrors(view: View) {
        val mailEditText = view.findViewById<EditText>(R.id.mailValue)
        val usernameEditText = view.findViewById<EditText>(R.id.usernameValue)
        val phoneEditText = view.findViewById<EditText>(R.id.phoneValue)

        mailEditText.hint = "Adresse email"
        usernameEditText.hint = "Nom d'utilisateur"
        phoneEditText.hint = "Téléphone"

        mailEditText.setHintTextColor(resources.getColor(android.R.color.darker_gray))
        usernameEditText.setHintTextColor(resources.getColor(android.R.color.darker_gray))
        phoneEditText.setHintTextColor(resources.getColor(android.R.color.darker_gray))
    }

    fun DatePicker.getDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        return calendar.time
    }
}
