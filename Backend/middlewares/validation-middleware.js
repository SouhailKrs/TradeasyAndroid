
const validationRule = {
    "email": "required|email|exist:User,email",
    "username": "required|string|exist:User,username",
    "phone": "required|string",
    "password": "required|string|min:6|confirmed|strict",
    "gender": "string"
}