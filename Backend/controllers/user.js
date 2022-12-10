import User from "../models/user.js";
import bcrypt from "bcryptjs";
import session from "express-session";
import jwt from "jsonwebtoken";
//mailgun
import mailgun from "mailgun-js";
const DOMAIN = "sandbox507e3894f4c144a99bfbe463744c5c8e.mailgun.org";
const MAILGUN_API_KEY = "e7b8db7cd772a6ccccd2ac5faf874ee4-48c092ba-1b9ee90c";
const mg = mailgun({ apiKey: MAILGUN_API_KEY, domain: DOMAIN });

export function putOnce(req, res) {
  let newUser = {};
  if (req.file == undefined) {
    newUser = {
      username: req.body.username,
      phoneNumber: req.body.phoneNumber,
      email: req.body.email,
      password: req.body.password,
    };
  } else {
    newUser = {
      username: req.body.username,
      phoneNumber: req.body.phoneNumber,
      email: req.body.email,
      password: req.body.password,
    };
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

//logout
export const logout = (req, res) => {
  res.cookie("jwt", "", { maxAge: 1 });
  res.header("jwt", "");
  res.send({
    message: "Logout successful",
  });
};

export function login(req, res) {
  const { username, email, phoneNumber, password } = req.body;
  User.findOne({
    $or: [
      { username: username },
      { email: email },
      { phoneNumber: phoneNumber },
    ],
  }).then((user) => {
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
          // id: user._id,
          username: user.username,
        },
        "access_secret",
        {
          expiresIn: "1w",
        }
      );

      res.cookie("jwt", accessToken, {
        httpOnly: true,
        maxAge: 24 * 60 * 60 * 1000 * 7, // 1 day
      });
      res.header("jwt", accessToken);

      user.save();
      res.send({ data: user, token: accessToken });
      console.log(accessToken);
    });
  });
}
export function register(req, res) {
  const { username, phoneNumber, email, password, profilePicture } = req.body;
  User.findOne({
    $or: [
      { username: username },
      { email: email },
      { phoneNumber: phoneNumber },
    ],
  })
    .then((user) => {
      if (user) {
        if (user.email === email) {
          return res.status(423).json({ message: "Email already exists" });
        } else if (user.username === username) {
          return res.status(421).json({ message: "Username already exists" });
        } else if (user.phoneNumber === phoneNumber) {
          return res
            .status(422)
            .json({ message: "Phone number already exists" });
        }
      }
      return bcrypt.hash(password, 12).then((hashedPassword) => {
        const user = new User({
          username,
          phoneNumber,
          email,
          password: hashedPassword,
          profilePicture,
        });
        const emailToken = jwt.sign({ id: user._id }, "email_secret", {
          expiresIn: "1d",
        });
        const data = {
          from: "noreply@Tradeasy.tn",
          to: email,
          subject: "Tradeasy Email Verification",
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
              </body>`,
        };
        mg.messages().send(data, function (error, body) {
          console.log(body);
        });
        const accessToken = jwt.sign(
          {
            id: user.username,
          },
          "access_secret",
          {
            expiresIn: "1w",
          }
        );

        res.cookie("jwt", accessToken, {
          httpOnly: true,
          maxAge: 24 * 60 * 60 * 1000 * 7, // 1 week
        });

        user.save();

        res.send({ data: user, token: accessToken });
      });
    })
    .catch((err) => {
      console.log(err);
    });
}

//email verification
export function verifyEmail(req, res) {
  const { emailToken } = req.body;
  if (emailToken) {
    jwt.verify(emailToken, "email_secret", (err, decodedToken) => {
      if (err) {
        res.status(400).json({ error: "Incorrect or expired link" });
      }
      const { id } = decodedToken;
      User.findById(id).then((user) => {
        user.isVerified = true;
        user.save();
        res.status(200).json({ message: "Email verified" });
      });
    });
  } else {
    res.status(400).json({ error: "Incorrect or expired link" });
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
      const resetToken = jwt.sign({ id: user._id }, "reset_secret", {
        expiresIn: "1d",
      });
      const data = {
        from: "noreply@Tradeasy.tn",
        to: email,
        subject: "Tradeasy Password Reset",
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
          </body>`,
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

// export function forgotPassword(req, res) {
//   const { email, username, phoneNumber } = req.body;
//   User.findOne({
//     $or: [
//       { username: username },
//       { email: email },
//       { phoneNumber: phoneNumber },
//     ],
//   }).then((user) => {
//     if (!user) {
//       return res.status(404).json({ error: "User doesn't exist" });
//     }
//     console.log(user);
//     //check if cookie exists
//     const otp = req.headers['otp']
//     if (req.cookies.otp === undefined) {
// console.log(req.cookies.otp)
//       const otp = generateRandomNumber();



//       const data = {
//         from: "noreply@tradeasy.tn",
//         to: user.email,
//         subject: "Tradeasy Password Reset",
//         html: ` <style>
//             .code {
//               background-color: #4CAF50; /* Green */
//               border: none;
//               color: white;
//               padding: 15px 32px;
//               text-align: center;
//               text-decoration: none;
//               display: inline-block;
//               font-size: 16px;
//             }
//             </style>
//             <head>
//             <h1>Reset your password</h1>
//             </head>
//           <body>
//           <h2>Hi ${username}</h2>
//           <p>Enter the following code to reset your password</p>
//           <P> Your code is ${otp} </P>
//             </body>`,
//       };
//       mg.messages().send(data, function (error, body) {
//         console.log(body);
//       });

//       //store otp in cookie
//       res.cookie("otp", otp, {
//         httpOnly: true,
//         maxAge: 60 * 1000 * 1 // 1 minute
//       });
//       res.status(200).json({ message: "Email sent" });
//     }
//     else {
//       res.status(400).json({ error: "Please wait for 1 minute" });
//     }
//   })
//     .catch((err) => {
//       console.log(err);
//     }
//     );
// }

//forgot password otp
export function forgotPassword(req, res) {
  const { email, username, phoneNumber } = req.body;
  User.findOne({
    $or: [
      { username: username },
      { email: email },
      { phoneNumber: phoneNumber },
    ],
  }).then((user) => {
    if (!user) {
      return res.status(404).json({ error: "User doesn't exist" });
    }
    
    //check if cookie exists

      const otp = Math.floor(100000 + Math.random() * 900000)



      const data = {
        from: "noreply@tradeasy.tn",
        to: user.email,
        subject: "Tradeasy Password Reset",
        html: ` <style>
            .code {
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
          <h2>Hi ${username}</h2>
          <p>Enter the following code to reset your password</p>
          <P> Your code is ${otp} </P>
            </body>`,
      };
      mg.messages().send(data, function (error, body) {
        console.log(body);
      });

      //jwt token for otp

      const otpToken = jwt.sign
        (
          { id: user._id, otp: otp },
          "otp_secret",
          {
            expiresIn: "5m",
          }
        );
        res.cookie('otpToken', otpToken, {
          httpOnly: true,
          maxAge: 60 * 1000, // 1 minute
        });

        res.header('otpToken', otpToken)
        console.log(otpToken)
        res.status(200).json({ message: "Email sent" });
    
    
  })
    .catch((err) => {
      console.log(err);
    }
    );
}

//verify otp
export function verifyOtp(req, res) {
  const { username, email, phoneNumber, otp, password } = req.body;
  const  otpToken  = req.cookies.otpToken;
  User.findOne({
    $or: [
      { username : username },
      { email : email },
      { phoneNumber : phoneNumber },
    ],
  }).then((user) => {
    if (!user) {
      return res.status(404).json({ error: "User doesn't exist" });
    }
    jwt.verify(otpToken, "otp_secret", (err, decodedToken) => {
      if (err) {
        return res.status(400).json({ error: "expired OTP or OTP doens't exist" });
      }
      else if (decodedToken.id != user._id) {
        return res.status(400).json({ error: "No password reset for this user" });
      }
     
      else {
        const otpToken = decodedToken.otp
        if (otpToken == otp && user.password.length > 8) {
          bcrypt.hash(password, 12).then((hashedPassword) => {
            user.password = hashedPassword;
            user.save();
            res.clearCookie("otpToken");
            res.header('otpToken', '')
            res.status(200).json({ message: "Password updated" })
          })
        }
        else {
          console.log(otpToken)
          res.status(400).json({ error: "Incorrect code" })
        }
      }
    })
  })
    .catch((err) => {
      console.log(err);
    }
    );
}

      



// export function verifyOtp(req, res) {
//   const { username, email, phoneNumber, otp, password } = req.body;
//   const  otpToken  = req.cookies.otpToken;
//   jwt.verify(otpToken, "otp_secret", (err, decodedToken) => {
//     if (err) {
//       console.log(err.message);
//       res.status(400).json({ error: " expired" });
//     }
//     else {
//       console.log(decodedToken);
//       User.findOne({
        
//     $or: [
//       { username: username },
//       { email: email },
//       { phoneNumber: phoneNumber },
//     ],
//    } )
//         .then((user) => {
//           if (!user) {
//             return res.status(404).json({ error: "User doesn't exist" });
//           }
//           const otpToken = decodedToken.otp
//           const id = decodedToken.id
          
//           console.log(id)
//           console.log(user._id)
//           console.log(otpToken)
//           console.log(otp);
//         console.log(otpToken === otp);
//         console.log(id === user._id);
//         console.log(user.password.length > 8);
       

//           if (otpToken == otp && id == user._id && user.password.length > 8) {
            
//             bcrypt.hash(password, 12).then((hashedPassword) => {
//               user.password = hashedPassword;
//               user.save();
//               res.clearCookie("otpToken");
//               res.header('otpToken', '')
//               res.status(200).json({ message: "Password updated" });
//             });
//           }
//           else {
//             res.status(400).json({ error: "Incorrect code" });
           
//           }
//         })
//         .catch((err) => {
//           console.log(err);
//         });
//     }
//   });
// }
  








//new password


export function newPassword(req, res) {
  const { resetToken, newPassword } = req.body;
  if (resetToken) {
    jwt.verify(resetToken, "reset_secret", (err, decodedToken) => {
      if (err) {
        res.status(400).json({ error: "Incorrect or expired link" });
      }
      const { id } = decodedToken;
      User.findById(id).then((user) => {
        bcrypt.hash(newPassword, 12).then((hashedPassword) => {
          user.password = hashedPassword;
          user.save();
          res.status(200).json({ message: "Password updated" });
        });
      });
    });
  } else {
    res.status(400).json({ error: "Incorrect or expired link" });
  }
}

/*export function verifyEmail(req, res) {
    const { token } = req.body;
    try {
      const payload = jwt.verify(token, "email_secret");
      if (!payload) {
        return res.status(401).json({
          message: "Authentication failed",
        });
      }
      User.findById(payload.id)
  
  
        .then((user) => {
          if (!user) {
            return res.status(401).json({
              message: "Authentication failed",
            });
          }
          user.isVerified = true;
          user.save();
          res.status(200).send({ message: "Email verified" });
        })
        .catch((err) => {
          res.status(501).json({ error: err });
        });
    } catch (err) {
      res.status(500).json({ error: err });
    }
  }*/

export function generateRandomNumber() {
  var result = "";
  var characters =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  var charactersLength = characters.length;
  for (var i = 0; i < 10; i++) {
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
    } else {
      res.status(400).json({ error: "Incorrect password" });
    }
  });
}

