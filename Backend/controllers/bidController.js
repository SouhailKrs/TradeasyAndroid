import Product from "../models/product.js";
import User from "../models/user.js";
import bid from "../models/bid.js";

import { sendNotification, notificationContent } from "../firebase-config.js";

// BID
export const placeBid = async (req, res) => {
  try {
    const user = req.user;
    const { product_id, bid_amount } = req.body;
    const product = await Product.findById(product_id);
    const seller = await User.findById(product.user_id);
    Product.findById(product_id).then((p) => {
      if (p.for_bid === true) {
        if (p.bid_end_date > new Date().getTime() && p.price < bid_amount) {
          const newBid = new bid({
            user_id: user,
            product_id: p,
            bid_amount: bid_amount,
          }); //update product price

          p.bidded = true;
          p.price = bid_amount;
          p.save();
          newBid.save();
          res.json({ data: newBid });
          sendNotification(
            seller.notificationToken,
            notificationContent("New bid", "You have a new bid on your product")
          );
          seller.notifications.push({
            title: ` new bid on ${product.name}`,
            description: `  ${user.username} has placed a bid on your product`,
            date: new Date().getTime(),
          });
          seller.save();
        } else {
          res.json({ msg: "Bid not created1" });
        }
      } else {
        res.json({ msg: "Bid not created" });
      }
    });
  } catch (error) {
    res.status(500).json(error.message);
  }
};
