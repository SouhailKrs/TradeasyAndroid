import jwt from "jsonwebtoken";
import User from "../models/user.js";

// check json web token exists & is verified
export const requireAuth = (req, res, next) => {
  //TODO: CHANGE FROM COOKIE TO HEADER WHEN MOVING TO ANDROID
  const token = req.cookies.jwt;
 //const token = req.headers['jwt']
  console.log(token)

  if (token) {
    jwt.verify(token, "access_secret", async (err, decodedToken) => {
      if (err) {
        res.status(400).send({ message: err.message });
      } else {
        let user = await User.findOne({ username: decodedToken.username });
        req.user = user;
        next();
      }
    });
  } else {
    console.log("No token");
    res.status(400).send({ message: "not authorized" });
  }
};
