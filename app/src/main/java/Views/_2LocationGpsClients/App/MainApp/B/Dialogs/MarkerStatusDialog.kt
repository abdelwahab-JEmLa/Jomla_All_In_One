package Views._2LocationGpsClients.App.MainApp.B.Dialogs

/*
@Composable
fun MarkerStatusDialog(
    viewModel: ViewModelInitApp,
    selectedMarker: Marker?,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    if (selectedMarker == null) return

    Dialog(onDismissRequest = onDismiss) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = selectedMarker.title ?: "Client",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                // New Command Mode Button
                StatusButton(
                    text = "Mode Commande",
                    icon = Icons.Default.ShoppingCart,
                    onClick = {
                        coroutineScope.launch {
                            viewModel.mapsHandler.updateStatueClient(
                                selectedMarker,
                                DernierEtatAAffiche.ON_MODE_COMMEND_ACTUELLEMENT
                            )
                            selectedMarker.updateA_AppSettingsSaverModel()
                            onDismiss()
                        }
                    }
                )
                StatusButton(
                    text = "Client Absent",
                    icon = Icons.Default.Person,
                    onClick = {
                        coroutineScope.launch {
                            viewModel.mapsHandler.updateStatueClient(
                                selectedMarker,
                                DernierEtatAAffiche.CLIENT_ABSENT
                            )
                            onDismiss()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                StatusButton(
                    text = "Avec Marchandise",
                    icon = Icons.Default.ShoppingCart,
                    onClick = {
                        coroutineScope.launch {
                            viewModel.mapsHandler.updateStatueClient(
                                selectedMarker,
                                DernierEtatAAffiche.AVEC_MARCHANDISE
                            )
                            onDismiss()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                StatusButton(
                    text = "Fermé",
                    icon = Icons.Default.Lock,
                    onClick = {
                        coroutineScope.launch {
                            viewModel.mapsHandler.updateStatueClient(
                                selectedMarker,
                                DernierEtatAAffiche.FERME
                            )
                            onDismiss()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Fermer")
                }
            }
        }
    }
}

@Composable
private fun StatusButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(text)
        }
    }
}
           */
