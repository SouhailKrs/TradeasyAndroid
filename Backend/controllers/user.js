import User from "../models/user.js";
import bcrypt from "bcryptjs";
import parsePhoneNumber from 'libphonenumber-js'
const jwt = require('jsonwebtoken');

export function login(req, res) {
  //login with username or email or phone number
  const { username, email, phoneNumber, password } = req.body;
  User.findOne({
    $or: [
      { username: username },
      { email: email },
      { phoneNumber: phoneNumber },
    ],
  })
    .then((user) => {
      if (!user) {
        return res.status(401).json({
          message: "Authentication failed",
        });
      }
      bcrypt.compare(password, user.password, (err, result) => {
        if (err) {
          return res.status(401).json({
            message: "Authentication failed",
          });
        }
        const accessToken = jwt.sign(
          {
            id: user._id,
          },
          "access_secret",
          {
            expiresIn: "30s",
          }
        );
        const refreshToken = jwt.sign(
          {
            id: user._id,
          },
          "refresh_secret",
          {
            expiresIn: "1w",
          }
        );
        res.cookie('accessToken', accessToken, {
          httpOnly: true,
          maxAge: 24 *  60 * 60 * 1000  // 1 day
        });
        res.cookie('refreshToken', refreshToken, {
          httpOnly: true,
            maxAge: 7 * 24 * 60 * 60 * 1000  // 1 week
        });
        res.send({
          message: "Authentication successful",
        });

      });
    })
}
export const AuthenticatedUser = (req, res, next) => {
  try{
    const accessToken = req.cookies.accessToken;
    const payload = jwt.verify(accessToken, "access_secret");
    if (!payload) {
      return res.status(401).json({
        message: "Authentication failed",
      });
    }
    User .findById(payload.id)
    .then((user) => {
      if (!user) {
        return res.status(401).json({
          message: "Authentication failed",
        });
      }
      const{password,createdAt,updatedAt,__v, ...others} = user._doc;
      res.send(others);
    })
    .catch((err) => {
      res.status(500).json({
        error: err,
      });
    });
  }catch(err){
    res.status(500).json({
      error: err,
    });
  }
}

export const Refresh = (req, res) => {
  try{
    const refreshToken = req.cookies.refreshToken;
    const payload = jwt.verify(refreshToken, "refresh_secret");
    if (!payload) {
      return res.status(401).json({
        message: "Authentication failed",
      });
    }
    const accessToken = jwt.sign(
      {
        id: payload.id,
      },
      "access_secret",
      {
        expiresIn: "30s",
      }
    );
    res.cookie('accessToken', accessToken, {
      httpOnly: true,
      maxAge: 7 * 24 * 60 * 60 * 1000  // 1 week
    });
    res.send({
      message: "Authentication successful",
    });
  }
  catch(err){
    res.status(500).json({
      error: err,
    });
  }
}
//logout
export const logout = (req, res) => {
  res.clearCookie('accessToken');
  res.clearCookie('refreshToken');
  res.send({
    message: "Logout successful",
  });
}






function between(x, min, max) {
  if (x >= min && x <= max)
    return true;
}

/*export function register(req, res) {
  const { username, phoneNumber, email, password } = req.body;
  const validateEmail = require('email-validator').validate;

  const TN='+216';
  const twoNumbers = parseInt(phoneNumber.substring(4, 6));
  

   User.findOne({ $or: [{ username: username }, { email: email }, { phoneNumber: phoneNumber }] })
    .then((user) => {
      if (user) {
        if(user.username === username && user.email === email && user.phoneNumber === phoneNumber){
          return res.status(400).json({ message: "Username, email and phone number already exist" });
        }
      if (user.email === email) {
        return res.status(423).json({ message: "Email already exists" });
      }
      else if (user.username === username) {
        return res.status(421).json({ message: "Username already exists" });
      }
      else if (user.phoneNumber === phoneNumber) {
        return res.status(422).json({ message: "Phone number already exists" });
      }
    }
      else if(isNaN(phoneNumber) || !(phoneNumber.toString().substring(0, 4)===TN) || phoneNumber.toString().length!=12 || between(twoNumbers, 1,19 )|| between(twoNumbers, 30,39 ) || between(twoNumbers, 60,89 )){
        
        return res.status(424).json({ message: "Invalid phone number" });
    
      }
      else if(!validateEmail(email)){
        console.log(x,isValid);
        return res.status(425).json({ message: "Invalid Email" });
    
      }
      return bcrypt
        .hash(password, 12)
        .then((hashedPassword) => {
          const user = new User({
            username,
            phoneNumber,
            email,
            password: hashedPassword,
          });
          const token = jwt.sign(
            {
             user: user,
            },
            "secret",
            {
              expiresIn: "1h",
            }
          );
            user.token = token;
            user.save();
        })
        .then((result) => {
          res.status(201).json({ 
            message: "User created successfully",
            user: user,
        });
          
        });
    })
    .catch((err) => {
      console.log(err);
    });
}*/
export function register(req, res) {
  const { username, phoneNumber, email, password } = req.body;
  User.findOne({ $or: [{ username: username }, { email: email }, { phoneNumber: phoneNumber }] })
    .then((user) => {
      if (user) {
        if (user.email === email) {
          return res.status(423).json({ message: "Email already exists" });
        }
        else if (user.username === username) {
          return res.status(421).json({ message: "Username already exists" });
        }
        else if (user.phoneNumber === phoneNumber) {
          return res.status(422).json({ message: "Phone number already exists" });
        }
      }
      return bcrypt
        .hash(password, 12)
        .then((hashedPassword) => {
          const user = new User({
            username,
            phoneNumber,
            email,
            password: hashedPassword,
          });
          console.log(between(twoNumbers, 60, 89));
          user.save();
        })
        .then((result) => {
          res.status(201).json({ message: "User created successfully" });
        });
    })
    .catch((err) => {
      console.log(err);
    });
}

export function putOnce(req, res) {
  let newUser = {};
  if (req.file == undefined) {
    newUser = {
      username: req.body.username,
      phoneNumber: req.body.phoneNumber,
      email: req.body.email,
      password: req.body.password,
    }
  }
  else {
    newUser = {
      username: req.body.username,
      phoneNumber: req.body.phoneNumber,
      email: req.body.email,
      password: req.body.password,
    }
  }
  User.findByIdAndUpdate(req.params.id, newUser)
    .then((doc1) => {
      User.findById(req.params.id)
        .then((doc2) => {
          res.status(200).json(doc2);
        })
        .catch((err) => {
          res.status(500).json({ error: err });
        });
    })
    .catch((err) => {
      res.status(500).json({ error: err });
    });
}

export function getUser(req, res) {
  res.status(200).send(req.session.user);
}