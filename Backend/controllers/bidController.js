import Product from "../models/product.js";
import User from "../models/user.js";
import Bid from "../models/bid.js";

import { sendNotification, notificationContent } from "../firebase-config.js";

// BID
export const placeBid = async (req, res) => {
  try {
    let lastBidder = ""
    const user = req.user;
    const { product_id, bid_amount } = req.body;
    // get the user id of last user who bade on the product
    const lastBid = await Bid.findOne
      ({ product_id: product_id }).sort({ _id: -1 });
    if (lastBid) {
      const bidder = await User.findById(lastBid.user_id);
      lastBidder = bidder;

    }

    else {
      console.log("no last bidder");
    }
    // get all users id who bade on the product

    const product = await Product.findOne({
      _id: product_id,
      user_id: { $ne: user._id },
    });
    if (product) {
      const seller = await User.findById(product.user_id);
      // dont let the same user bid twice on the same product
      const bidExists = await Bid.findOne({
        user_id: user._id,
        product_id: product._id,
      });

      if (bidExists) {
        res.status(500).json({ "message": `You have already bade on ${product.name}` });
      } else {
        Product.findById(product_id).then((p) => {
          if (p.for_bid === true) {
            if (p.bid_end_date > new Date().getTime() && p.price < bid_amount) {
              const newBid = new Bid({
                user_id: user._id,
                product_id: p._id,
                bid_amount: bid_amount,
              }); //update product price
              // remove all bids with the same product id
              Bid.deleteMany({ product_id: p._id }, function (err) {
                if (err) console.log(err);
                console.log("Successful deletion");
              });
              p.bidded = true;
              p.price = bid_amount;
              p.save();
              newBid.save();
              res.json({ data: newBid });
              if (lastBidder.notificationToken != "" && lastBidder.notificationToken != null & lastBidder != "") {
                sendNotification(
                  lastBidder.notificationToken,
                  notificationContent(
                    "Bid",
                    `You have been outbidded on ${product.name}`
                  )
                );
              }

              //get the lat user who bade on the product
              if (lastBidder != "") {
                lastBidder.notifications.push({
                  title: `Bid `,
                  description: `You have been outbidded on ${product.name}`,
                  date: new Date().getTime(),
                });
                lastBidder.save();
              }
              if (seller.notificationToken != "" && seller.notificationToken != null) {
                sendNotification(
                  seller.notificationToken,
                  notificationContent(
                    "New bid",
                    `New bid on ${product.name}`
                  )
                );
              }
              // get the lat user who bade on the product
              seller.notifications.push({
                title: `new bid on ${product.name}`,
                description: `${user.username} placed a bid on your product`,
                date: new Date().getTime(),
              });

              seller.save();
            } else {
              res.json({ "message": "Bid not created1" });
            }
          } else {
            res.json({ "message": "Bid not created" });
          }
        });
      }
    } else {
      res.json({ "message": "product not found" });
    }
  } catch (error) {
    res.json({ "message": error.message });
  }
};


// automatic function that says hello world every 5 seconds

//function to find user by id and set notification list 
// export const setNotificationList = async (req, res) => {
//   try {
//     const user = 
//     await User.findById("63aad19748983686a2411621");
// console.log("uuser is " + user);
// const notifications = {
//   title: `Bid `,
//   description: `You have been outbidded on `,
//   date: new Date().getTime(),

// }

//     user.notifications.push(notifications);
//     user.save();

//   } catch (error) {
//     res.json({ "message": error.message });
//   }
// }

// setInterval( setNotificationList, 1000);