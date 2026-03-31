# API Examples

## 1. Signup as customer

```http
POST /gateway/auth/signup
Content-Type: application/json

{
  "fullName": "Asha Patel",
  "email": "asha@example.com",
  "password": "Password@123",
  "phoneNumber": "9999999999"
}
```

## 2. Signup as admin

```http
POST /gateway/auth/signup
Content-Type: application/json

{
  "fullName": "Admin User",
  "email": "admin@example.com",
  "password": "Password@123",
  "phoneNumber": "9000000000",
  "roles": ["ADMIN"]
}
```

## 3. Create delivery

```http
POST /gateway/deliveries
Authorization: Bearer <JWT>
Content-Type: application/json

{
  "senderAddress": {
    "contactName": "Asha Patel",
    "phoneNumber": "9999999999",
    "line1": "12 MG Road",
    "line2": "Near Metro",
    "city": "Pune",
    "state": "MH",
    "country": "India",
    "postalCode": "411001"
  },
  "receiverAddress": {
    "contactName": "Riya Sharma",
    "phoneNumber": "8888888888",
    "line1": "44 Connaught Place",
    "line2": "",
    "city": "Delhi",
    "state": "DL",
    "country": "India",
    "postalCode": "110001"
  },
  "packageDetails": {
    "description": "Electronics",
    "weightKg": 2.5,
    "lengthCm": 20,
    "widthCm": 15,
    "heightCm": 10,
    "fragile": true
  },
  "serviceType": "EXPRESS",
  "scheduledPickupDate": "2026-03-30",
  "declaredValue": 2500
}
```
