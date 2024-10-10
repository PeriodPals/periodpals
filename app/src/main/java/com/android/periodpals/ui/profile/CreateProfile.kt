package com.android.periodpals.ui.profile

import android.app.Activity
import android.content.Intent
import android.icu.util.GregorianCalendar
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.periodpals.R
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import io.ktor.util.hex

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CreateProfile() {
  var name by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var age by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }

  var profileImageUri by remember { mutableStateOf<Uri?>(null) }
  var context = LocalContext.current

  val launcher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
              if (result.data == null || result.data?.data == null) {
                Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
                return@rememberLauncherForActivityResult
              }
              profileImageUri = result.data?.data
            }
          }

  Scaffold(
      modifier = Modifier
          .fillMaxSize(),
      content = { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp).padding(padding),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Box(
              modifier =
                  Modifier
                      .size(124.dp)
                      .clip(shape = RoundedCornerShape(100.dp))
                      .background(color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(100.dp))
                      .clickable {
                          val pickImageIntent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
                          launcher.launch(pickImageIntent)
                      }) {
                profileImageUri?.let {
                  GlideImage(
                        model = it,
                        contentDescription = "profile picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(124.dp)
                            .background(color=Color.White ,shape= CircleShape)
                  )
                }
                    ?:
                    Image(
                        painter = painterResource(id = R.drawable.generic_avatar),
                        contentDescription = "profile picture",
                        modifier = Modifier.size(124.dp)
                    )
              }

          Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Mandatory",
                style =
                    TextStyle(
                        fontSize = 20.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight(500),
                        letterSpacing = 0.2.sp,
                    ),
                modifier = Modifier.align(Alignment.TopStart),
            )

          }

          OutlinedTextField(
              value = email,
              onValueChange = { email = it },
              label = { Text("Email") },
              placeholder = { Text("Enter your email") },
          )

          OutlinedTextField(
              value = age,
              onValueChange = { age = it },
              label = { Text("Age") },
              placeholder = { Text("Enter your date of birth") },
          )

          Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Your profile",
                style =
                    TextStyle(
                        fontSize = 20.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight(500),
                        letterSpacing = 0.2.sp,
                    ),
                modifier = Modifier.align(Alignment.TopStart),
            )
          }

          OutlinedTextField(
              value = name,
              onValueChange = { name = it },
              label = { Text("Displayed Name") },
              placeholder = { Text("Enter your name") },
          )

          OutlinedTextField(
              value = description,
              onValueChange = { description = it },
              label = { Text("Description") },
              placeholder = { Text("Enter a description") },
              modifier = Modifier.height(150.dp))

          Button(
              onClick = {
                val calendar = GregorianCalendar()
                val parts = age.split("/")
                if (parts.size == 3) {
                  try {
                    calendar.set(parts[2].toInt(), parts[1].toInt() - 1, parts[0].toInt())
                    return@Button
                  } catch (_: NumberFormatException) {}
                }
                Toast.makeText(context, "Invalid date of birth", Toast.LENGTH_SHORT).show()
              },
              enabled = true,
              modifier =
                  Modifier.padding(0.dp)
                      .width(84.dp)
                      .height(40.dp)
                      .background(
                          color = Color(0xFF65558F), shape = RoundedCornerShape(size = 100.dp)),
              colors = ButtonDefaults.buttonColors(Color(0xFF65558F))) {
                Text(
                    "Save",
                    color = Color.White,
                )
              }
        }
      })
}
