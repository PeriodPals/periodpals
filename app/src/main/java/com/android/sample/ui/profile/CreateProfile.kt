package com.android.sample.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R

@Composable
fun CreateProfile() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { padding ->


            Column (
                modifier = Modifier.fillMaxSize().padding(16.dp).padding(padding),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                Icon(
                    Icons.Filled.Person,
                    contentDescription = "profile picture",
                    Modifier.size(190.dp)
                        .background(color = Color(0xFFD5DCFD), shape = RoundedCornerShape(size = 100.dp)),
                    tint = Color(0xFF65558F),
                )

                Box(  modifier = Modifier
                    .fillMaxWidth()
                ) {
                    Text(
                        text = "Mandatory",
                        style = TextStyle(
                            fontSize = 20.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight(500),
                            letterSpacing = 0.2.sp,
                        ),
                        modifier = Modifier.align(Alignment.TopStart),
                    )
                }

                OutlinedTextField(
                    value = "Enter your email",
                    onValueChange = {},
                    label = { Text("Email") },
                    )

                OutlinedTextField(
                    value = "Enter your date of birth",
                    onValueChange = {},
                    label = { Text("Age") },
                    )

                Box(  modifier = Modifier
                    .fillMaxWidth()
                ) {
                    Text(
                        text = "Your profile",
                        style = TextStyle(
                            fontSize = 20.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight(500),
                            letterSpacing = 0.2.sp,
                        ),
                        modifier = Modifier.align(Alignment.TopStart),
                        )
                }

                OutlinedTextField(
                    value = "Enter your name",
                    onValueChange = {},
                    label = { Text("Displayed Name") },
                )

                OutlinedTextField(
                    value = "Describe yourself",
                    onValueChange = {},
                    label = { Text("Description") },
                )


                Button(

                    onClick = {},
                    enabled = true,
                    modifier = Modifier
                        .padding(0.dp)
                        .width(84.dp)
                        .height(40.dp)
                        .background(color = Color(0xFF65558F), shape = RoundedCornerShape(size = 100.dp)),
                    colors = ButtonDefaults.buttonColors(Color(0xFF65558F))
                ) {
                    Text("Save",
                        color = Color.White,)
                }

            }
        }
    )
}