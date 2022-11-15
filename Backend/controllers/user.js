import User from "../models/user.js";
import bcrypt from "bcryptjs";
import session from "express-session";
import jwt from "jsonwebtoken";
//mailgun
import mailgun from "mailgun-js";
const DOMAIN = 'sandbox507e3894f4c144a99bfbe463744c5c8e.mailgun.org';
const MAILGUN_API_KEY = 'e7b8db7cd772a6ccccd2ac5faf874ee4-48c092ba-1b9ee90c';
  const mg = mailgun({apiKey: MAILGUN_API_KEY, domain: DOMAIN});

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
  
  export const AuthenticatedUser = (req, res, next) => {
    try {
      const accessToken = req.cookies.accessToken;
      const payload = jwt.verify(accessToken, "access_secret");
      if (!payload) {
        return res.status(401).json({
          message: "Authentication failed",
        });
      }
      User.findById(payload.id)
        .then((user) => {
          if (!user) {
            return res.status(401).json({
              message: "Authentication failed1",
            });
          }
          const { createdAt, updatedAt, __v, _id, token, ...userData } = user._doc;
          session.user = userData;
          req.session.save();
  
          res.status(200).send({ data: session.user });
  
        })
        .catch((err) => {
          res.status(500).json({ error: err });
        });
    } catch (err) {
      res.status(500).json({ error: err });
    }
  };
  
  export const Refresh = (req, res) => {
    try {
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
          expiresIn: "1w",
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
    catch (err) {
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
        bcrypt.compare(password, user.password).then((isMatch) => {
          if (!isMatch) {
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
              expiresIn: "1w",
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
            maxAge: 24 * 60 * 60 * 1000  // 1 day
          });
          res.cookie('refreshToken', refreshToken, {
            httpOnly: true,
            maxAge: 7 * 24 * 60 * 60 * 1000  // 1 week
          });
  
          session.user = user;
          user.save();
          res.send({ data: user }
  
          );
  
        });
      })
  }
  export function register(req, res) {
    const { username, phoneNumber, email, password, profilePicture } = req.body;
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
              profilePicture,
            });
            const emailToken = jwt.sign({ id: user._id }, "email_secret", { expiresIn: "1d" });
            const data = {
  
              from: 'noreply@Tradeasy.tn',
              to: email,
              subject: 'Tradeasy Email Verification',
              html: ` <style>
              .button {
                background-color: #4CAF50; /* Green */
                border: none;
                color: white;
                padding: 15px 32px;
                text-align: center;
                text-decoration: none;
                display: inline-block;
                font-size: 16px;
              }
              </style>
              <head>
              <h1>Verify your email</h1>
              </head>
            <body>
              <p>Click the following link to verify your email</p>
               <button><a href=" http://localhost:9090/user/verify/${emailToken}">Verify</a></button>
              </body>`
            };
            mg.messages().send(data, function (error, body) {
              console.log(body);
            });
            const accessToken = jwt.sign(
              {
                id: user._id,
              }, "access_secret"
              ,
              {
                expiresIn: "1w",
              }
            );
            const refreshToken = jwt.sign(
              {
  
              },
              "refresh_secret",
              {
                expiresIn: "1w",
              }
            );
            res.cookie('accessToken', accessToken, {
              httpOnly: true,
              maxAge: 24 * 60 * 60 * 1000  // 1 day
            });
            res.cookie('refreshToken', refreshToken, {
              httpOnly: true,
              maxAge: 7 * 24 * 60 * 60 * 1000  // 1 week
            });
  
            session.user = user;
            user.save();
            res.json({ data: user });
          })
  
      })
      .catch((err) => {
        console.log(err);
      });
  }
  
  //email verification
  export function verifyEmail(req, res) {
    const { emailToken } = req.params;
    if (emailToken) {
      jwt.verify(emailToken, "email_secret", function (err, decodedToken) {
        if (err) {
          res.status(400).json({ error: "Incorrect or Expired link" });
        }
        const { id } = decodedToken;
        User.findById(id).then((user) => {
          if (!user) {
            return res.status(404).json({ error: "User does not exist" });
          }
          user.isVerified = true;
          user.save();
          res.json({ message: "Email successfully verified" });
        });
      });
    }
    else {
      return res.json({ message: "Something went wrong" });
    }
  }

  //reset password
  export function resetPassword(req, res) {
    const { email } = req.body;
    User.findOne({ email: email })
      .then((user) => {
        if (!user) {
          return res.status(404).json({ error: "User doesn't exist" });
        }
        const resetToken = jwt.sign({ id: user._id }, "reset_secret", { expiresIn: "1d" });
        const data = {
          from: 'noreply@tradeasy.tn',
          to: email,
          subject: 'Tradeasy Password Reset',
          html: ` <style>
          .button {
            background-color: #4CAF50; /* Green */
            border: none;
            color: white;
            padding: 15px 32px;
            text-align: center;
            text-decoration: none;
            display: inline-block;
            font-size: 16px;
            
          }
          </style>
          <head>
          <h1>Reset your password</h1>
          </head>
        <body>
          <p>Click the following link to reset your password ${generateRandomNumber()} "</p>
          <button><a href=" http://localhost:9090/user/reset/${resetToken}">Reset</a></button>
          </body>`
        };
        mg.messages().send(data, function (error, body) {
          console.log(body);
        });
        res.status(200).json({ message: "Email sent" });
      })
      .catch((err) => {
        console.log(err);
      });
  }
  export function newPassword(req, res) {
    const { resetToken, newPassword } = req.body;
    if (resetToken) {
      jwt.verify(resetToken, "reset_secret", (err, decodedToken) => {
        if (err) {
          res.status(400).json({ error: "Incorrect or expired link" });
        }
        const { id } = decodedToken;
        User.findById(id)
          .then((user) => {
            bcrypt.hash(newPassword, 12).then((hashedPassword) => {
              user.password = hashedPassword;
              user.save();
              res.status(200).json({ message: "Password updated" });
            });
          });
      });
    }
    else {
      res.status(400).json({ error: "Incorrect or expired link" });
    }
  }
  
  
  export function getUser(req, res) {
    const accessToken = req.cookies.accessToken;
    console.log("aaa: " + accessToken);
    res.status(200).send({ data: session.user });
  
  }
  // reset password
  //login
  
  export function generateRandomNumber() {
    var result           = '';
    var characters       = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    var charactersLength = characters.length;
    for ( var i = 0; i < 10; i++ ) {
        result += characters.charAt(Math.floor(Math.random() * charactersLength));
    }
    console.log(result);
    return result;
   
  }
  // update password
  export function updatePassword(req, res) {
    const { currentPassword, newPassword } = req.body;
  
    const user = session.user;
    bcrypt.compare(currentPassword, user.password).then((doMatch) => {
      if (doMatch) {
        bcrypt.hash(newPassword, 12).then((hashedPassword) => {
          user.password = hashedPassword;
          user.save();
          res.status(200).json({ data: user });
        });
      }
      else {
        res.status(400).json({ error: "Incorrect password" });
      }
    });
  }