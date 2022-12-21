import User from "../models/user.js";
import bcrypt from "bcryptjs";
import session from "express-session";
import jwt from "jsonwebtoken";
//mailgun
import mailgun from "mailgun-js";

const DOMAIN = "sandbox507e3894f4c144a99bfbe463744c5c8e.mailgun.org";
const MAILGUN_API_KEY = "e7b8db7cd772a6ccccd2ac5faf874ee4-48c092ba-1b9ee90c";
const mg = mailgun({ apiKey: MAILGUN_API_KEY, domain: DOMAIN });
const maxAge = 3 * 24 * 60 * 60;
const createToken = (id) => {
  return jwt.sign({ id }, "access_secret", { expiresIn: maxAge });
};

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
    "message": "Logout successful",
  });
};

export const login = async (req, res) => {
  const { username, email, phoneNumber, password, notificationToken } =
    req.body;
  try {
    const user = await User.findOne({
      $or: [
        { username: username },
        { email: email },
        { phoneNumber: phoneNumber },
      ],
    });
    if (user) {
      const auth = await bcrypt.compare(password, user.password);
      if (auth) {
        const token = createToken(user._id);
        res.cookie("jwt", token, { httpOnly: true, maxAge: maxAge * 1000 });
        res.header("jwt", token);
        // update notification token
        const newUser = await User.findByIdAndUpdate(user._id, {
          notificationToken: notificationToken,
        });
        await newUser.save();
        res.status(200).json({
          data: newUser,
          token: token,
        });
      } else {
        res.status(400).json({ "message": "Incorrect password" });
      }
    } else {
      res.status(404).json({ "message": "invalid credentials" });
    }
  } catch (err) {
    res.status(400).json({ "message": err.message });
  }
};

export const register = async (req, res) => {
  const {
    username,
    phoneNumber,
    email,
    password,
    profilePicture,
    notificationToken,
    countryCode,
  } = req.body;
  try {
    const user = await User.findOne({
      $or: [
        { username: username },
        { email: email },
        { phoneNumber: phoneNumber },
      ],
    });
    if (user) {
      if (user.email === email) {
        return res.status(423).json({ "message": "Email already exists" });
      } else if (user.username === username) {
        return res.status(421).json({ "message": "Username already exists" });
      } else if (user.phoneNumber === phoneNumber) {
        return res.status(422).json({ "message": "Phone number already exists" });
      }
    } else {
      const salt = await bcrypt.genSalt(10);
      const hashedPassword = await bcrypt.hash(password, salt);

      const newUser = new User({
        username: username,
        phoneNumber: phoneNumber,
        email: email,
        password: hashedPassword,
        profilePicture: profilePicture,
        notificationToken: notificationToken,
        countryCode: countryCode,
      });
      newUser.save().then(() => {
        const token = createToken(newUser._id);

        res.cookie("jwt", token, { httpOnly: true, maxAge: maxAge * 1000 });
        res.header("jwt", token);
        res.status(201).json({ data: newUser, token: token });
      });
    }
  } catch (err) {
    res.status(400).json({ "message": err.message });
  }
};

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
// update username and check if username already exists in database
export async function updateUsername(req, res) {
  const { newUsername } = req.body;
  const currentUser = req.user;
  const user = await User.findOne({
    username: newUsername,
  }).exec();
  if (user) {
    return res.status(400).json({ "message": "Username already exists" });
  }
  currentUser.username = newUsername;
  currentUser.save();
  res.status(200).json({ data: currentUser });
}
//verify email
export function verifyEmail(req, res) {
  const { token } = req.query;
  try {
    const payload = jwt.verify(token, "email_secret");
    if (!payload) {
      return res.status(401).json({
        "message": "Authentication failed",
      });
    }
    User.findById(payload.id)
      .then((user) => {
        if (!user) {
          return res.status(401).json({
            "message": "Authentication failed",
          });
        }
        user.isVerified = true;
        user.save();
        res.status(200).send({ "message": "Email verified" });
      })
      .catch((err) => {
        res.status(501).json({ error: err });
      });
  } catch (err) {
    res.status(500).json({ error: err });
  }
}

export function changeEmail(req, res) {
  const { email, password } = req.body;
  try {
    User.findOne({ email: email }).then((user) => {
      if (user) {
        return res.status(400).json({ error: "Email already exists" });
      }
    });
    User.findById(req.user._id).then((user) => {
      if (!user) {
        return res.status(400).json({ error: "Login to change email" });
      }
      bcrypt.compare(password, user.password).then((doMatch) => {
        if (!doMatch) {
          return res.status(400).json({ error: "Incorrect password" });
        }
        //check email exists
        User;

        user.email = email;
        user.update();
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
            <head> <title></title> <meta http-equiv="Content-Type" content="text/html; charset=utf-8" /> <meta name="viewport" content="width=device-width, initial-scale=1"> <meta http-equiv="X-UA-Compatible" content="IE=edge" /> <style type="text/css"> @media screen { @font-face { font-family: 'Montserrat'; font-style: normal; font-weight: 400; src: local('Montserrat'), local('Montserrat'), url(https://fonts.google.com/share?selection.family=Montserrat:ital,wght@1,200); } @font-face { font-family: 'Montserrat'; font-style: normal; font-weight: 700; src: local('Montserrat Bold'), local('Montserrat-Bold'), url(https://fonts.google.com/share?selection.family=Montserrat%20Subrayada:wght@700%7CMontserrat:ital,wght@1,200); } /* CLIENT-SPECIFIC STYLES / body, table, td, a { -webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; } table, td { mso-table-lspace: 0pt; mso-table-rspace: 0pt; } img { -ms-interpolation-mode: bicubic; } / RESET STYLES / img { border: 0; height: auto; line-height: 100%; outline: none; text-decoration: none; } table { border-collapse: collapse !important; } body { height: 100% !important; margin: 0 !important; padding: 0 !important; width: 100% !important; } / iOS BLUE LINKS / a[x-apple-data-detectors] { color: inherit !important; text-decoration: none !important; font-size: inherit !important; font-family: inherit !important; font-weight: inherit !important; line-height: inherit !important; } / MOBILE STYLES / @media screen and (max-width:600px) { h1 { font-size: 32px !important; line-height: 32px !important; } } / ANDROID CENTER FIX */ div[style*="margin: 16px 0;"] { margin: 0 !important; } </style>
  </head>
  <body style="background-color: #f4f4f4; margin: 0 !important; padding: 0 !important;">
  </head> <!-- HIDDEN PREHEADER TEXT --> <div style="display: none; font-size: 1px; color: #fefefe; line-height: 1px; font-family: 'Montserrat'Helvetica, Arial, sans-serif; max-height: 0px; max-width: 0px; opacity: 0; overflow: hidden;"> Go anywhere with anywheel!</div> <table border="0" cellpadding="0" cellspacing="0" width="100%"> <!-- LOGO --> <tr> <td bgcolor="#f4f4f4" align="center"> <table border="0" cellpadding="0" cellspacing="0" width="100%" style="max-width: 600px;"> <tr> <td align="center" valign="top" style="padding: 40px 10px 40px 10px;"> </td> </tr> </table> </td> </tr> <tr> <td bgcolor="#f4f4f4" align="center" style="padding: 0px 10px 0px 10px;"> <table border="0" cellpadding="0" cellspacing="0" width="100%" style="max-width: 600px;"> <tr> <td bgcolor="#ffffff" align="center" valign="top" style="padding: 40px 20px 20px 20px; border-radius: 2px 2px 0px 0px; color: #AADB1E; font-family: 'Londrina Solid'Helvetica, Arial, sans-serif; font-size: 45px; font-weight: 700; letter-spacing: 2px; line-height: 48px;"> <h1 style="font-size: 40px; font-weight:700; margin: w-50;">Tradeasy</h1> </td> </tr> </table> </td> </tr> <tr> <td bgcolor="#f4f4f4" align="center" style="padding: 0px 10px 0px 10px;"> <table border="0" cellpadding="0" cellspacing="0" width="100%" style="max-width: 600px;"> <tr> <td bgcolor="#ffffff" align="center" style="padding: 20px 30px 40px 30px; color: #000000; font-family:'Montserrat bold' Helvetica, Arial, sans-serif; font-size: 16px; font-weight:600; line-height: 25px;"> <p >Kindly verify your email to complete your account registration.</p> </td> </tr> <tr> <td bgcolor="#ffffff" align="left"> <table width="100%" border="0" cellspacing="0" cellpadding="0"> <tr> <td bgcolor="#ffffff" align="center" style="padding: 20px 30px 60px 30px;"> <table border="0" cellspacing="0" cellpadding="0"> <tr> <td align="center" style="border-radius: 30px;" bgcolor="#000000"><a href="http://localhost:9090/user/verify?token=${emailToken}" target="_blank" style="font-size: 20px; font-family: 'Montserrat Bold'Helvetica, Arial, sans-serif; color: #ffffff; text-decoration: none; color: #ffffff; text-decoration: none; padding: 10px 55px; border-radius: 2px; display: inline-block;">VERIFY NOW</a></td> </tr> </table> </td> </tr> </table> </td> </tr> <!-- COPY --> <tr> <td bgcolor="#ffffff" align="center" style="padding: 0px 30px 0px 30px; color: #000000; font-family:'Montserrat'Helvetica, Arial, sans-serif; font-size: 14px; font-weight:550; line-height: 25px;"><p style="margin: 0;">Alternatively, you can copy this URL to your browser:</p> </td> </tr> <!-- COPY --> <tr> <td bgcolor="#ffffff" align="center" style="padding: 20px 30px 20px 30px; color: #666666; font-family:'Montserrat'Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 550; line-height: 25px;"> <p style="margin: 0;"><a href="#" target="_blank" style="color: #29ABE2;">http://localhost:9090/user/verify?token=${emailToken}</a></p> </td> </tr> <tr> <td bgcolor="#ffffff" align="center" style="padding: 0px 30px 20px 30px; color: #000000; font-family:'Montserrat'Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 400; line-height: 25px;"> <p style="margin: 0;"> <p style="margin: 0;">The link will be valid for the next 24 hours.</p> </td> </tr> <tr> <td bgcolor="#ffffff" align="center" style="padding: 0px 30px 40px 30px; border-radius: 0px 0px 4px 4px; color: #000000; font-family:'Montserrat'Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 400; line-height: 25px;">   </td> </tr> <tr> <td bgcolor="#ffffff" align="center" style="padding: 0px 30px 40px 30px; border-radius: 0px 0px 4px 4px; color: #333333; font-family:'Montserrat'Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 400; line-height: 25px;"> <a href="facebook.com/tradeasy"> <img src="https://img.icons8.com/ios-glyphs/30/000000/facebook-new.png"/> </a> <a href="instagram.com/tradeasy"> <img src="https://img.icons8.com/material-outlined/30/000000/instagram-new.png"/></a> </td> </tr> </table> </td> </tr> <tr> <td bgcolor="#f4f4f4" align="center" style="padding: 0px 10px 0px 10px;"> <table border="0" cellpadding="0" cellspacing="0" width="100%" style="max-width: 600px;"> <tr> <td bgcolor="#f4f4f4" align="center" style="padding: 0px 30px 30px 30px; color: #666666; font-family: 'Lato', Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 400; line-height: 18px;"> <br> <p style="margin: ;"><a href="#" target="_blank" style="color: #111111; font-weight: 700;"</p> </td> </tr> </table> </td> </tr> </table>
  </body>
  <button></button>
            </body>`,
      };
      mg.messages().send(data, function (error, body) {
        if (error) {
          return res.json({
            error: error.message,
          });
        }
        return res.json({
          "message": "Email has been sent, kindly activate your account",
        });
      });
    });
  } catch (err) {
    res.status(500).json({ error: err });
  }
}
//forgot password otp
export function forgotPassword(req, res) {
  const { email } = req.body;
  User.findOne({
   
     email: email 
      
  }).then((user) => {
    if (!user) {
      return res.status(404).json({ "message": "User doesn't exist" });
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
          <h2>Hi ${user.username}</h2>
          <p>Enter the following code to reset your password</p>
          <P> Your code is ${otp} </P>
            </body>`,
      };
      mg.messages().send(data, function (error, body) {
      });

      //jwt token for otp
      user.otp = otp;
      user.save();
     
      res.status(200).json({ "message": "Email sent" });
    }
   )
    .catch((err) => {
          res.json({ "message": err.message });
    }
    );
}
//set new password
export function resetPassword(req, res) {
  const {  email, otp, newPassword } = req.body;
  
  User.findOne({
   

      email: email ,
   
    
  })
    .then((user) => {
      if (!user) {
        return res.status(404).json({ "message": "User doesn't exist" });
      }
     else
      {if (user.otp == otp) {
        bcrypt.hash(newPassword, 12).then((hashedpassword) => {
          user.password = hashedpassword;
          user.otp = 0;
          user.save();
          res.status(200).json({ "message": "Password changed" });
        });
      }
      else {
        res.status(400).json({ "message": "Incorrect OTP" });
      }
    }
    })
    .catch((err) => {
     res.json({ "message": err.message });
    });
}

// verify otp
export function verifyOtp(req, res) {
  const { email,otp } = req.body;
 
  User.findOne({
    email: email,
  })
    .then((user) => {
      if (!user) {
        return res.status(404).json({ "message": "User doesn't exist" });
      }
      else
      {if (user.otp == otp) {
        res.status(200).json({ "message": "OTP verified" });
      }
      else {
        res.status(400).json({ "message": "Incorrect OTP" });
      }
    }
    })
    .catch((err) => {
     res.json({ "message": err.message });
    });
}

// verify username existance
export function verifyUsername(req, res) {
  const { newUsername } = req.body;
  User
    .findOne({
      username: newUsername,
    })
    .then((user) => {
      if (!user) {
        return res.status(404).json({ "message": "Username available" });
      }

      else {
      return res.status(200).json({ "message": "Username exists" });
      }}
    )
    .catch((err) => {
      res.json({ "message": err.message });
      }
    );
}