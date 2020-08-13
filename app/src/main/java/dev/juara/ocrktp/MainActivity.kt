package dev.juara.ocrktp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import dev.juara.ocrktp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.btnTakePicture.setOnClickListener {
            startActivityForResult(
                Intent(
                    this,
                    TakeActivity::class.java
                ), 1
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            binding.etNik.setText(data?.getStringExtra("nik") ?: " ")
            binding.etTempatLahir.setText(data?.getStringExtra("birthPlace") ?: " ")
            binding.etTglLahir.setText(data?.getStringExtra("birthDate") ?: " ")
            binding.etGender.setText(data?.getStringExtra("gender") ?: " ")
            binding.etNama.setText(data?.getStringExtra("name") ?: " ")
            binding.etAlamat.setText(data?.getStringExtra("address") ?: " ")
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}