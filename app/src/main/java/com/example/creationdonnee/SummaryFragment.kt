package com.example.creationdonnee

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

class SummaryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inscription_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rootView = requireView()

        val email = arguments?.getString("email") ?: ""
        val username = arguments?.getString("username") ?: ""
        val nameVal = arguments?.getString("name") ?: ""
        val phoneNumber = arguments?.getString("phoneNumber") ?: ""
        val password = arguments?.getString("password") ?: ""
        val hobbies = arguments?.getStringArrayList("hobbies") ?: ArrayList()

        val dateOfBirthInMillis = arguments?.getLong("dateOfBirth") ?: 0L
        val dateOfBirth = Date(dateOfBirthInMillis)

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(dateOfBirth)

        rootView.findViewById<TextView>(R.id.tv_email).text = "Email : $email"
        rootView.findViewById<TextView>(R.id.tv_username).text = "Nom d'utilisateur : $username"
        rootView.findViewById<TextView>(R.id.tv_nom).text = "Nom : $nameVal"
        rootView.findViewById<TextView>(R.id.tv_telephone).text = "Téléphone : $phoneNumber"
        rootView.findViewById<TextView>(R.id.tv_interets).text = "Centres d'intérêts : ${hobbies.joinToString(", ")}"

        rootView.findViewById<TextView>(R.id.tv_date_naissance).text = "Date de naissance : $formattedDate"

        val backButton = rootView.findViewById<Button>(R.id.btn_retour)
        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        val validateButton = rootView.findViewById<Button>(R.id.btn_valider)
        validateButton.setOnClickListener {
            val dbHelper = DatabaseHelper(requireContext())
            val user = User(
                mail = email,
                name = nameVal,
                username = username,
                phoneNumber = phoneNumber,
                dateOfBirth = dateOfBirth,
                hobbies = hobbies,
                password = password
            )
            dbHelper.addUser(user)

            val intent = Intent(requireContext(), PlaningActivity::class.java)
            intent.putExtra("USER_ID",dbHelper.getUserIdByUsernameOrEmail(user.username))
            startActivity(intent)
            requireActivity().finish()
        }
    }
}
