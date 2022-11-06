import User from "../models/user.js";
import bcrypt from "bcryptjs";


export function login(req, res) {
  const { username, password } = req.body;

  User.findOne({ username: username })
    .then((user) => {
      if (!user) {
        return res.status(404).json({ message: "User not found" });
      }
      bcrypt.compare(password, user.password).then((doMatch) => {
        if (doMatch) {
          req.session.user = user;
          req.session.save();
          res.status(200).json({ 
            message: "User signed in successfully",
            user: user
           });
        } else {
          res.status(401).json({ message: "Invalid credentials" });
          res.status(404).json({ message: "Incorrect password" });
        }
      });
    })
    .catch((err) => {
      console.log(err);
    });
}

export function register(req, res) {
  const { username, phoneNumber, email, password } = req.body;

  User.findOne({ email: email })
    .then((user) => {
      if (user) {
        return res.status(422).json({ message: "User already exists" });
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
          return user.save();
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
  if(req.file == undefined) {
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
export function logout(req, res) {
  req.session.destroy();
  res.status(200).json({ message: "User logged out successfully" });
}
export function getUser(req, res) {
  res.status(200).send(req.session.user);
}