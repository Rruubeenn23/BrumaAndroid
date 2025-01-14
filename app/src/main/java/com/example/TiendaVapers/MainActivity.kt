package com.example.prueba

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.prueba.ui.theme.PruebaTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import coil.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.storage


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PruebaTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(cartViewModel: CartViewModel = viewModel()) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val isDarkTheme = isSystemInDarkTheme()

    // Definir el color para el tema oscuro
    val blueColor = if (isDarkTheme) Color(0xFF5E81E0) else Color(0xFF80D3F7)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight()
                    .background(blueColor), // Aplicar color azul aquí
                contentAlignment = Alignment.CenterStart
            ) {
                DrawerContent(navController, drawerState, scope, blueColor)
            }
        },
        scrimColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.32f)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "BRUMA", style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.width(200.dp)) // Espaciado entre el texto y la imagen
                            Image(
                                painter = painterResource(id = R.drawable.logo),
                                contentDescription = "Logo",
                                modifier = Modifier.size(90.dp)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            bottomBar = {
                BottomNavigationBar(navController = navController, cartViewModel = cartViewModel) // Color se maneja internamente en BottomNavigationBar
            }
        ) { paddingValues ->
            BackgroundImage() // Imagen de fondo
            NavHostContainer(navController, Modifier.padding(paddingValues), cartViewModel, blueColor)
        }
    }
}



@Composable
fun DrawerContent(
    navController: NavHostController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    blueColor: Color
) {
    val itemsTop = listOf(
        DrawerItem("home", Icons.Default.Home, "Inicio", blueColor)
    )
    val itemsCenter = listOf(
        DrawerItem("orders", Icons.Default.List, "Pedidos", blueColor),
        DrawerItem("account", Icons.Default.AccountCircle, "Cuenta", blueColor)
    )
    val itemsBottom = listOf(
        DrawerItem("logout", Icons.Default.ExitToApp, "Cerrar sesión", blueColor)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp, top = 16.dp), // Espaciado interno para el menú
        verticalArrangement = Arrangement.SpaceBetween // Distribuye los bloques
    ) {
        // Bloque superior: Inicio
        Column (Modifier.padding(vertical = 15.dp)) {
            itemsTop.forEach { item ->
                DrawerNavigationItem(item, navController, drawerState, scope, blueColor)
            }
        }

        // Bloque central: Pedidos y Cuenta
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsCenter.forEach { item ->
                DrawerNavigationItem(item, navController, drawerState, scope, blueColor)
            }
        }

        // Bloque inferior: Ayuda y Cerrar Sesión
        Column {
            itemsBottom.forEach { item ->
                DrawerNavigationItem(item, navController, drawerState, scope, blueColor)
            }
        }
    }
}
@Composable
fun DrawerNavigationItem(
    item: DrawerItem,
    navController: NavHostController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    menuBackgroundColor: Color
) {
    val context = LocalContext.current
    val isSelected = currentRouteDrawer(navController) == item.route

    Box(
        modifier = Modifier
            .fillMaxWidth() // Esto asegura que el fondo ocupe el ancho completo
            .background(
                color = if (isSelected) Color.Black else menuBackgroundColor,
                shape = MaterialTheme.shapes.large
            )
            .border(
                width = 10.dp,
                shape = MaterialTheme.shapes.small,
                color = if (isSelected) Color.Black else menuBackgroundColor,
            )
    ) {
        NavigationDrawerItem(
            icon = { Icon(item.icon, contentDescription = item.label) },
            label = { Text(item.label) },
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = Color.Black, // Color de fondo cuando está seleccionado
                unselectedContainerColor = Color.Transparent, // Fondo transparente para no cubrir el fondo completo
                selectedTextColor = Color.White, // Color del texto seleccionado
                unselectedTextColor = Color.Black // Color del texto no seleccionado
            ),
            selected = isSelected,
            onClick = {
                scope.launch { drawerState.close() }
                if (item.route != "logout") {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                } else {
                    // Manejar el cierre de sesión
                    println("Cerrando sesión...")

                    val intent = Intent(context, LoginActivity::class.java)
                    context.startActivity(intent)

                    if (context is Activity) {
                        context.finish()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth() // Asegura que el item ocupe todo el ancho
        )
    }
}





@Composable
fun BackgroundImage() {
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundImage = if (isDarkTheme) {
        painterResource(id = R.drawable.dark_background) // Imagen para el modo oscuro
    } else {
        painterResource(id = R.drawable.light_background) // Imagen para el modo claro
    }
    Image(
        painter = backgroundImage, // Imagen de fondo
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize(),
        contentScale = ContentScale.Crop // Escala para que ocupe todo el fondo

    )
}

@Composable
fun NavHostContainer(navController: NavHostController, modifier: Modifier, cartViewModel: CartViewModel, backgroundMenuColor: Color) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier,
    ) {
        composable("home") { HomeScreen(products = products, navController = navController, cartViewModel = cartViewModel) }
        composable("cart") { CartScreen(cartViewModel = cartViewModel, backgroundMenuColor = backgroundMenuColor, navController = navController) }
        composable("payment_confirmation") { PaymentSummaryScreen(cartViewModel = cartViewModel) }
        composable("account") { AccountScreen(backgroundMenuColor, navController = navController) }
        composable("orders") { OrdersScreen(cartViewModel, backgroundMenuColor )  }
        composable("help") { HelpScreen() }
    }
}


@Composable
fun BottomNavigationBar(navController: NavHostController, cartViewModel: CartViewModel) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Cart,
        BottomNavItem.Account
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary, // Color directamente desde el theme
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        val currentRoute = currentRoute(navController)
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    if (item.route == BottomNavItem.Cart.route) {
                        // Agregar Badge solo para el carrito
                        BadgedBox(badge = {
                            if (cartViewModel.cartItemCount > 0) {
                                Badge { Text("${cartViewModel.cartItemCount}") }
                            }
                        }) {
                            Icon(item.icon, contentDescription = item.label)
                        }
                    } else {
                        Icon(item.icon, contentDescription = item.label)
                    }
                },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}


@Composable
fun HomeScreen(products: List<Product>, navController: NavHostController, cartViewModel: CartViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center // Centra todo el contenido en el contenedor principal
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp), // Espaciado entre elementos
            horizontalAlignment = Alignment.CenterHorizontally, // Asegura que las tarjetas estén centradas
            modifier = Modifier.fillMaxWidth() // Ajusta el ancho del LazyColumn
        ) {
            items(products) { product ->
                ProductCard(product = product, cartViewModel = cartViewModel)
            }
        }
    }
}

@Composable
fun CartScreen(cartViewModel: CartViewModel, backgroundMenuColor: Color, navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            "Carrito de compras",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        // Contenedor para los ítems del carrito que hace scroll
        LazyColumn(
            modifier = Modifier
                .weight(1f) // Ocupa el espacio restante
                .fillMaxWidth()
        ) {
            items(cartViewModel.cartItems) { product ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(backgroundMenuColor)
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = product.imageRes),
                            contentDescription = product.name,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${product.name}", style = MaterialTheme.typography.bodyMedium)
                            Text("${product.price}€", style = MaterialTheme.typography.bodySmall, color = Color.Black)
                        }
                        Button(
                            onClick = {
                                cartViewModel.removeFromCart(product)
                            },
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text("Eliminar")
                        }
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                "Total: ${cartViewModel.cartItems.sumOf { it.price }}€",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Button(
                onClick = {
                    navController.navigate("payment_confirmation")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Pagar ahora")
            }
        }
    }
}


@Composable
fun PaymentSummaryScreen(cartViewModel: CartViewModel) {
    val totalAmount = cartViewModel.cartItems.sumOf { it.price } // Calcula el total

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Resumen de Pago", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Total: $${totalAmount}",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { cartViewModel.placeOrder(totalAmount) }) {
            Text("Pagar Ahora")
        }
    }
}



@Composable
fun ProductCard(product: Product, cartViewModel: CartViewModel) {
    val isDarkTheme = isSystemInDarkTheme()

    // Definir los colores de fondo según el tema

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(), // Ocupa todo el ancho disponible
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(), // Asegúrate de centrar dentro del ancho disponible
            horizontalAlignment = Alignment.CenterHorizontally // Centra el contenido horizontalmente
        ) {
            Image(
                painter = painterResource(id = product.imageRes),
                contentDescription = product.name,
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally) // Centra la imagen horizontalmente
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(product.name, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
            Text(product.description, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "$${product.price}",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { cartViewModel.addToCart(product) }) {
                Text("Añadir al carrito")
            }
        }
    }
}

@Composable
fun AccountScreen(menuBackgroundColor: Color, navController: NavHostController) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var username by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }

    // Verificar si el usuario ya tiene datos configurados
    LaunchedEffect(currentUser) {
        currentUser?.uid?.let { userId ->
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        username = document.getString("username") ?: ""
                        fullName = document.getString("fullName") ?: ""
                    }
                }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray.copy(alpha = 0.1f)), // Fondo general
        contentAlignment = Alignment.Center // Colocar la tarjeta en el centro
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(0.9f),
            colors = CardDefaults.cardColors(containerColor = menuBackgroundColor),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Título
                Text(
                    "Configuración de Cuenta",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Mostrar correo
                Text(
                    text = "Correo: ${currentUser?.email ?: "Usuario no autenticado"}",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )


                // Botón para cambiar contraseña
                Button(
                    onClick = {
                        currentUser?.let {
                            FirebaseAuth.getInstance().sendPasswordResetEmail(it.email ?: "")
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Correo de restablecimiento enviado", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Error al enviar correo de restablecimiento", Toast.LENGTH_SHORT).show()
                                }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Text("Cambiar Contraseña")
                }

                // Botón de cerrar sesión
                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(context, LoginActivity::class.java)
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar Sesión")
                }
            }
        }
    }
}








@Composable
fun currentRouteDrawer(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

@Composable
fun OrdersScreen(cartViewModel: CartViewModel, menuBackgroundColor: Color) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text("Pedidos", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
        LazyColumn {
            itemsIndexed(cartViewModel.orders) { index, order ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    // Mostrar el número de pedido
                    Text("Pedido #${index + 1}", style = MaterialTheme.typography.bodyLarge)

                    // Mostrar el total del pedido
                    Text("Total: $${order.totalAmount}", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Iterar sobre los productos del pedido
                    order.cartItems.forEach { product ->
                        // Contenedor con fondo para cada item
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(menuBackgroundColor, shape = MaterialTheme.shapes.medium) // Fondo con borde redondeado
                                .padding(8.dp) // Espaciado interno
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Imagen a la izquierda
                                Image(
                                    painter = painterResource(id = product.imageRes),
                                    contentDescription = product.name,
                                    modifier = Modifier.size(50.dp) // Tamaño pequeño para la imagen
                                )

                                // Texto a la derecha
                                Column(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .wrapContentWidth()
                                ) {
                                    Text(
                                        product.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.align(Alignment.Start)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}


@Composable
fun HelpScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Pantalla de Ayuda")
    }
}

class CartViewModel : ViewModel() {
    private val _cartItems = mutableStateListOf<Product>()
    val cartItems: List<Product> get() = _cartItems
    private val _orders = mutableStateListOf<Order>()
    val orders: List<Order> get() = _orders

    val cartItemCount: Int
        get() = _cartItems.size

    fun addToCart(product: Product) {
        _cartItems.add(product)
    }

    fun placeOrder(totalAmount: Double) {
        // Crear el pedido solo si el total es mayor a 0
        if (totalAmount > 0) {
            val order = Order(cartItems = _cartItems.toList(), totalAmount = totalAmount)
            _orders.add(order)

            // Vaciar el carrito después de realizar el pedido
            _cartItems.clear()
        }
    }
    fun removeFromCart(product: Product) {
        _cartItems.remove(product) // Usa el método `remove` para eliminar un elemento específico.
    }
}

data class Order(val cartItems: List<Product>, val totalAmount: Double)





// Lista de productos simulados
val products = listOf(
    Product(0, "Vaper Hielo Negro, Fruta del Dragón y Fresa", "Vaper VapSolo de 15000 caladas", R.drawable.blackicedragonfruitstrawberry15, 15.00),
    Product(1, "Vaper Arándanos Azules, Cereza y Arándanos Rojos", "Vaper VapSolo de 15000 caladas", R.drawable.blueberrycherrycranberry15, 15.00),
    Product(2, "Vaper Arándanos Azules Helados", "Vaper VapSolo de 15000 caladas", R.drawable.blueberryice15, 15.00),
    Product(3, "Vaper Frutas Azules", "Vaper VapSolo de 15000 caladas", R.drawable.bluerazz15, 15.00),
    Product(4, "Vaper Frambuesa y Sandía", "Vaper VapSolo de 15000 caladas", R.drawable.raspberrywatermelon15, 15.00),
    Product(5, "Vaper Red Bull", "Vaper VapSolo de 15000 caladas", R.drawable.redbull15, 15.00),
    Product(6, "Vaper Skittles", "Vaper VapSolo de 15000 caladas", R.drawable.skittles15, 15.00),
    Product(7, "Vaper Fresa y Plátano", "Vaper VapSolo de 15000 caladas", R.drawable.strawberrybanana15, 15.00),
    Product(8, "Vaper Fresa y Kiwi", "Vaper VapSolo de 15000 caladas", R.drawable.strawberrykiwi15, 15.00),
    Product(9, "Vaper \"Summer Dream\"", "Vaper VapSolo de 15000 caladas", R.drawable.summerdream15, 15.00),
    Product(10, "Vaper Sandía Helada", "Vaper VapSolo de 15000 caladas", R.drawable.watermelonice15, 15.00),
)

// Clase de modelo de producto

// Item de la barra de navegación inferior


// Función para obtener la ruta actual de la navegación
@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

data class DrawerItem(val route: String, val icon: ImageVector, val label: String, var color : Color)
data class Product(val id: Int, val name: String, val description: String, val imageRes: Int, val price: Double)