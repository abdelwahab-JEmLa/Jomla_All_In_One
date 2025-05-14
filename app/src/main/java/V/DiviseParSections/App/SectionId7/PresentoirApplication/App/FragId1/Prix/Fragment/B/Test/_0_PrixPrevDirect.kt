package V.DiviseParSections.App.SectionId7.PresentoirApplication.App.FragId1.Prix.Fragment.B.Test

/*
@Preview(showBackground = true)
@Composable
fun PrixPrevDirect() {
    FragmentMain()
}

@Composable
private fun FragmentMain() {
    val viewModel = remember {
        TarificationViewModel()
    }

    var outputModel by remember {
        mutableStateOf(viewModel.getOutputModel())
    }

    MaterialTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            TabRow(selectedTabIndex = 0) {
                Tab(
                    text = { Text("UI") },
                    selected = true,
                    onClick = { }
                )
                Tab(
                    text = { Text("Logs") },
                    selected = false,
                    onClick = { }
                )
            }

            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "D_TarificationInfos Dashboard (Direct Model)",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        items(outputModel.produits) { produit ->
                            val produitName =
                                viewModel.getSqlProduit(produit.infosId)?.nom
                                    ?: "Produit ${produit.infosId}"
                            ProduitCard(
                                produit = produit,
                                produitName = produitName,
                                tarificationViewModel = viewModel
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }

                FloatingActionButton(
                    onClick = {
                        viewModel.addRandomTarification()
                        outputModel = viewModel.getOutputModel()
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Test Data")
                }
            }
        }
    }
}
                       */
