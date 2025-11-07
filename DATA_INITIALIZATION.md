# Initial Data - Hammai Group Tunisia

The system automatically creates initial test data on first startup.

---

## 👥 Users Created

### 1. System Administrator
- **Username:** `admin`
- **Password:** `Admin@123`
- **Role:** ADMIN
- **Email:** admin@hammai-group.tn
- **Full Name:** System Administrator
- **Access:** Full system administration

### 2. Responsible Manager
- **Username:** `responsible`
- **Password:** `Resp@123`
- **Role:** RESPONSIBLE
- **Email:** responsible@hammai-group.tn
- **Full Name:** Responsible Manager
- **Access:** Manage POS operations, verify sessions, reports

### 3. Cashier / POS User
- **Username:** `cashier`
- **Password:** `Cashier@123`
- **Role:** POS_USER
- **Email:** cashier@hammai-group.tn
- **Full Name:** Cashier User
- **Access:** Create sales, process payments, manage sessions

---

## 💳 Payment Methods Created

### 1. Cash (Espèces)
- **Code:** CASH
- **Name:** Espèces
- **Type:** CASH
- **Description:** Paiement en espèces
- **Active:** Yes

### 2. Credit Card (Carte de Crédit)
- **Code:** CREDIT_CARD
- **Name:** Carte de Crédit
- **Type:** CREDIT_CARD
- **Description:** Paiement par carte de crédit
- **Active:** Yes

### 3. Debit Card (Carte de Débit)
- **Code:** DEBIT_CARD
- **Name:** Carte de Débit
- **Type:** DEBIT_CARD
- **Description:** Paiement par carte de débit
- **Active:** Yes

### 4. Check (Chèque)
- **Code:** CHECK
- **Name:** Chèque
- **Type:** CHECK
- **Description:** Paiement par chèque
- **Requires Confirmation:** Yes
- **Active:** Yes

### 5. Mobile Payment (Paiement Mobile)
- **Code:** MOBILE_PAYMENT
- **Name:** Paiement Mobile
- **Type:** MOBILE_PAYMENT
- **Description:** Flooz, Orange Money, etc.
- **Active:** Yes

---

## 🧑‍💼 Customers Created

### 1. Retail Customer
- **Code:** CUST001
- **Name:** Client Détail
- **Email:** retail@example.com
- **Phone:** +216 98 123 456
- **Address:** Rue Habib Bourguiba
- **City:** Tunis
- **Country:** Tunisie
- **Type:** Retail
- **Credit Limit:** None

### 2. Hammai Group SARL
- **Code:** CUST002
- **Name:** Hammai Group SARL
- **Email:** contact@hammai-group.tn
- **Phone:** +216 71 234 567
- **Address:** Zone Industrielle
- **City:** Sfax
- **Country:** Tunisie
- **Tax ID:** 12345678-A
- **Type:** Wholesale
- **Credit Limit:** 50,000 TND

### 3. Corporate Customer
- **Code:** CUST003
- **Name:** Entreprise Tunisienne
- **Email:** comptabilite@entreprise.tn
- **Phone:** +216 71 345 678
- **Address:** Avenue Mohamed V
- **City:** Ariana
- **Country:** Tunisie
- **Tax ID:** 87654321-B
- **Type:** Corporate
- **Credit Limit:** 100,000 TND
- **Notes:** Clients corporate VIP

---

## 📦 Products Created

### 1. Produit Premium
- **Code:** PROD001
- **Name:** Produit Premium
- **Type:** PRODUCT
- **Unit Price:** 250.00 TND
- **Cost Price:** 180.00 TND
- **Stock:** 150 units
- **Min Stock:** 20 units
- **Barcode:** 1234567890123
- **Tax Rate:** 19% (Tunisia VAT)
- **Category:** Catégorie 1
- **Brand:** Hammai Brand
- **Unit:** PIECE
- **Taxable:** Yes

### 2. Produit Standard
- **Code:** PROD002
- **Name:** Produit Standard
- **Type:** PRODUCT
- **Unit Price:** 150.00 TND
- **Cost Price:** 100.00 TND
- **Stock:** 300 units
- **Min Stock:** 50 units
- **Barcode:** 1234567890124
- **Tax Rate:** 19%
- **Category:** Catégorie 1
- **Brand:** Hammai Brand
- **Unit:** PIECE
- **Taxable:** Yes

### 3. Produit Économique
- **Code:** PROD003
- **Name:** Produit Économique
- **Type:** PRODUCT
- **Unit Price:** 75.00 TND
- **Cost Price:** 50.00 TND
- **Stock:** 500 units
- **Min Stock:** 100 units
- **Barcode:** 1234567890125
- **Tax Rate:** 19%
- **Category:** Catégorie 2
- **Brand:** Hammai Brand
- **Unit:** PIECE
- **Taxable:** Yes

### 4. Service Installation
- **Code:** SERV001
- **Name:** Service Installation
- **Type:** SERVICE
- **Unit Price:** 350.00 TND
- **Cost Price:** 200.00 TND
- **Stock:** Unlimited
- **Tax Rate:** 19%
- **Category:** Services
- **Brand:** Hammai Services
- **Unit:** SERVICE
- **Taxable:** Yes

### 5. Pack Promo
- **Code:** PKG001
- **Name:** Pack Promo
- **Type:** PACKAGE
- **Unit Price:** 800.00 TND
- **Cost Price:** 600.00 TND
- **Stock:** 50 units
- **Min Stock:** 10 units
- **Tax Rate:** 19%
- **Category:** Offres Promotionnelles
- **Brand:** Hammai Promo
- **Unit:** PACK
- **Taxable:** Yes

---

## 🏢 Tunisia-Specific Configuration

### VAT (TVA)
- **Standard Rate:** 19% (applied to all taxable items)

### Currency
- **Primary:** Tunisian Dinar (TND)

### Cities
- Tunis (capital)
- Sfax (industrial)
- Ariana (north)

### Phone Format
- Country Code: +216
- Format: +216 XX XXX XXX

### Payment Methods
- Espèces (Cash)
- Carte (Credit/Debit Cards)
- Chèque (Check)
- Paiement Mobile (Flooz, Orange Money)

---

## 🔄 When Data is Created

The initial data is created automatically when:
1. Application starts for the first time
2. Database is empty (no existing data)
3. Each entity checks its count before creating

**Note:** Data is only created if the table is empty to prevent duplicates on restart.

---

## 🧪 Testing

After startup, you can login with any of the three users and test:

1. **Cashier login** → Create sales, add products, process payments
2. **Responsible login** → View reports, verify sessions, manage inventory
3. **Admin login** → Full system access, manage users, configure settings

---

## 📊 Statistics

- **Users:** 3 (Admin, Responsible, Cashier)
- **Payment Methods:** 5 (Cash, Cards, Check, Mobile)
- **Customers:** 3 (Retail, Wholesale, Corporate)
- **Products:** 5 (3 Products, 1 Service, 1 Package)

**Total Initial Records:** 16

---

## 🎯 Ready to Use!

Your POS system is now fully populated with realistic Tunisian data for Hammai Group. Start testing immediately!

