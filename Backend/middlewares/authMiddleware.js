import jwt from "jsonwebtoken";
import User from "../models/user.js";

// check json web token exists & is verified
const requireAuth = (req, res, next) => {
  //TODO: CHANGE FROM COOKIE TO HEADER WHEN MOVING TO ANDROID
//const token = req.cookies.jwt;

const token = req.headers['jwt']

  if (token) {
      jwt.verify(token, 'access_secret', async (err, decodedToken) => {
          if (err) {
              res.status(400).send({"message": err.message});
          } else {
              let user = await User.findById(decodedToken.id);
              res.locals.user = user;
              req.user = user;
              next();
          }
      });
  } else {
    res.locals.user = null;
    req.user = null;
   next();
      console.log('No token');
     // res.status(400).send({"message": "not authorized"});
  }
};

export {requireAuth};

