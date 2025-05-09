======== TESTING FILTERED DATES HISTORIQUE BY DAY ========

-- Filtered Transactions for Day: 2025-05-05 --
Found 0 transaction(s) for this day
No transactions found for date: 2025-05-05

======== FILTER TEST COMPLETED SUCCESSFULLY ========

======== TESTING FILTERED DATES HISTORIQUE BY DAY ========

-- Filtered Transactions for Day: 2025-05-04 --
Found 0 transaction(s) for this day
No transactions found for date: 2025-05-04

======== FILTER TEST COMPLETED SUCCESSFULLY ========

======== TESTING SqlDatasDatesHistoriqueTransactions TRANSACTIONS ========

-- Hierarchical Structure --
Semaine (2025-05-05): 2 jour(s)
  ├─ Jour 0 (2025-05-08): 4 transaction(s)
  │   Client ID: 177 (الاخ زواوي محمد) - 2 transaction(s)
  │  ├─ Transaction #0 (ID: 8, État: A_COMMANDE_CONFIRME, Time: 21:45)
  │  ├─ Transaction #1 (ID: 9, État: ON_MODE_COMMEND_ACTUELLEMENT, Time: 22:54)
  │   Client ID: 185 (الاخ محمد بن عمر) - 1 transaction(s)
  │  ├─ Transaction #2 (ID: 11, État: ACHETEUR_NON_DISPO, Time: 16:20)
  │   Client ID: 195 (الاخ حسن محمود) - 1 transaction(s)
  │  └─ Transaction #3 (ID: 13, État: FERME, Time: 13:45)
  └─ Jour 1 (2025-05-09): 4 transaction(s)
      Client ID: 174 (الاخ لعمري اسماعيل) - 2 transaction(s)
     ├─ Transaction #0 (ID: 1, État: A_COMMANDE_CONFIRME, Time: 12:30)
     ├─ Transaction #1 (ID: 2, État: ON_MODE_COMMEND_ACTUELLEMENT, Time: 14:15)
      Client ID: 180 (الاخ سليم بن علي) - 1 transaction(s)
     ├─ Transaction #2 (ID: 10, État: AVEC_MARCHANDISE, Time: 09:10)
      Client ID: 190 (الاخ أحمد خالد) - 1 transaction(s)
     └─ Transaction #3 (ID: 12, État: COMMANDE_LIVRAI, Time: 11:05)

======== TEST COMPLETED SUCCESSFULLY ========

======== TESTING mapSemainJours TRANSACTIONS ========

-- Hierarchical Structure --
Semaines (1):
  └─ Week: 2025-05-05 (2 days)
     ├─ Day: 2025-05-09 (4 transactions)
  │  ├─ Transaction: 1 (State: A_COMMANDE_CONFIRME)
  │  ├─ Transaction: 2 (State: ON_MODE_COMMEND_ACTUELLEMENT)
  │  ├─ Transaction: 10 (State: AVEC_MARCHANDISE)
  │  └─ Transaction: 12 (State: COMMANDE_LIVRAI)
     └─ Day: 2025-05-08 (4 transactions)
  │  ├─ Transaction: 8 (State: A_COMMANDE_CONFIRME)
  │  ├─ Transaction: 9 (State: ON_MODE_COMMEND_ACTUELLEMENT)
  │  ├─ Transaction: 11 (State: ACHETEUR_NON_DISPO)
  │  └─ Transaction: 13 (State: FERME)

======== TEST COMPLETED SUCCESSFULLY ========

======== TESTING DATES HISTORIQUE TRANSACTIONS ========

-- Hierarchical Structure --

-- Semaines (Weeks) --
Semaine (2025-05-05): 2 jour(s)
  ├─ Jour 0 (2025-05-08): 4 transaction(s)
  │   Client ID: 177 (الاخ زواوي محمد) - 2 transaction(s)
  │  ├─ Transaction #0 (ID: 8, État: A_COMMANDE_CONFIRME, Time: 21:45)
  │  ├─ Transaction #1 (ID: 9, État: ON_MODE_COMMEND_ACTUELLEMENT, Time: 22:54)
  │   Client ID: 185 (الاخ محمد بن عمر) - 1 transaction(s)
  │  ├─ Transaction #2 (ID: 11, État: ACHETEUR_NON_DISPO, Time: 16:20)
  │   Client ID: 195 (الاخ حسن محمود) - 1 transaction(s)
  │  └─ Transaction #3 (ID: 13, État: FERME, Time: 13:45)
  └─ Jour 1 (2025-05-09): 4 transaction(s)
      Client ID: 174 (الاخ لعمري اسماعيل) - 2 transaction(s)
     ├─ Transaction #0 (ID: 1, État: A_COMMANDE_CONFIRME, Time: 12:30)
     ├─ Transaction #1 (ID: 2, État: ON_MODE_COMMEND_ACTUELLEMENT, Time: 14:15)
      Client ID: 180 (الاخ سليم بن علي) - 1 transaction(s)
     ├─ Transaction #2 (ID: 10, État: AVEC_MARCHANDISE, Time: 09:10)
      Client ID: 190 (الاخ أحمد خالد) - 1 transaction(s)
     └─ Transaction #3 (ID: 12, État: COMMANDE_LIVRAI, Time: 11:05)

======== TEST COMPLETED SUCCESSFULLY ========

