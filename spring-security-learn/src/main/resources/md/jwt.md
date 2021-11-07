### JWT (json web token)

1. header

   令牌类型 + 签名算法 -> base64

   ```
   {
     "alg": "HS256",
     "typ": "JWT"
   }
   
   eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9
   ```

2. payload

   base64

   ```
   {
     "sub": "1234567890",
     "name": "John Doe",
     "admin": true
   }
   
   eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ
   ```

3. signature

   ```
   HMACSHA256(
     base64UrlEncode(header) + "." +
     base64UrlEncode(payload),
     secret)
     
   SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
   ```



