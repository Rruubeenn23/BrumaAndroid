package com.example.prueba

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Inicio")
    object Cart : BottomNavItem("cart", Icons.Default.ShoppingCart, "Carrito")
    object Account : BottomNavItem("account", Icons.Default.AccountCircle, "Cuenta")
}
