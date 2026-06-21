package ar.edu.uade.capturarecibosapp.data.repository

import android.util.Log
import ar.edu.uade.capturarecibosapp.data.SessionManager
import ar.edu.uade.capturarecibosapp.data.remote.AuthApiService
import ar.edu.uade.capturarecibosapp.data.remote.dto.*
import ar.edu.uade.capturarecibosapp.domain.model.User

class AuthRepository(private val apiService: AuthApiService) {

    suspend fun login(email: String, pass: String): Result<User> {
        return try {
            val response = apiService.login(AuthRequestDTO(email, pass))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Log.d("AuthRepository", "Login successful. UUID: ${body.id}")
                
                val user = body.toDomain()
                SessionManager.userId = user.uuid
                SessionManager.userEmail = user.email
                SessionManager.accessToken = body.accessToken
                SessionManager.refreshToken = body.refreshToken
                
                Result.success(user)
            } else {
                Result.failure(Exception("Error en el login: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Login exception", e)
            Result.failure(e)
        }
    }

    suspend fun registerUser(
        email: String, 
        pass: String, 
        name: String,
        birth: String,
        country: String,
        phone: String = ""
    ): Result<User> {
        return try {
            val request = AuthRequestDTO(
                email = email,
                password = pass,
                data = mapOf(
                    "name" to name,
                    "birth" to birth,
                    "country" to country,
                    "phone" to phone
                ) 
            )
            
            Log.d("AuthRepository", "Attempting signUp for: $email")
            val response = apiService.signUp(request)
            
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Log.d("AuthRepository", "SignUp successful. UUID: ${body.id}")
                
                val user = body.toDomain()
                SessionManager.userId = user.uuid
                SessionManager.userEmail = user.email
                SessionManager.accessToken = body.accessToken
                SessionManager.refreshToken = body.refreshToken
                
                Result.success(user)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error en el registro"
                Log.e("AuthRepository", "SignUp error: $errorMsg")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "SignUp exception", e)
            Result.failure(e)
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            val response = apiService.recoverPassword(mapOf("email" to email))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al recuperar contraseña"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun changePassword(password: String): Result<Unit> {
        return try{
            val response = apiService.changePassword(mapOf("password" to password))
            if(response.isSuccessful){
                Result.success(Unit)
            }
            else{
                Result.failure(Exception("Error al cambiar la contraseña"))
            }
        }
        catch(e: Exception){
            Result.failure(e)
        }
    }

    suspend fun sendRecoveryCode(email: String): Result<Unit> {
        return resetPassword(email)
    }
}
