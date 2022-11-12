import User from "../models/user.js";
import bcrypt from "bcryptjs";
import session from "express-session";
import jwt from "jsonwebtoken";
// dotenv
import dotenv from "dotenv";


dotenv.config();

const  TOKEN_SECRET="FdLQaCdEkeEkqWzv7xDu2taALFCEEOyHDKeDhTH0rib7lRPzhNhDcyavnyfP";




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
          message: "Authentication failed1",
        });
      }
      const{createdAt,updatedAt,__v,_id,token, ...userData} = user._doc; 
      session.user = userData;
      req.session.save();
      
  res.status(200).send({data:session.user});
      
    })
    .catch((err) => {
      res.status(500).json({ error: err });
    });
  }catch(err){
    res.status(500).json({ error: err });
  }
};

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
        expiresIn: "5s",
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
            expiresIn: "5s",
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
        user.token=accessToken;
        console.log("aaa: "+accessToken);
        session.user = user;
       user.updateOne({ token: accessToken }, { $set: { refreshToken: refreshToken } })
       user.save();
        res.send( {data:user}
         
        );

      });
    })
}
export function register(req, res) {
  const { username, phoneNumber, email, password,profilePicture } = req.body;
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
          const accessToken = jwt.sign(
            {
              id: user._id,
            },"access_secret"
            ,
            {
              expiresIn: "5s",
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
            maxAge: 24 *  60 * 60 * 1000  // 1 day
          });
          res.cookie('refreshToken', refreshToken, {
            httpOnly: true,
              maxAge: 7 * 24 * 60 * 60 * 1000  // 1 week
          });
         
        user.token=accessToken;
        session.user=user;
          user.save();
          res.json({data:user});
        })

    })
    .catch((err) => {
      console.log(err);
    });
}


// get user details


export function getUser(req, res) 
{const accessToken = req.cookies.accessToken;
  console.log("aaa: "+accessToken);
  res.status(200).send({data:session.user});

}
// reset password



