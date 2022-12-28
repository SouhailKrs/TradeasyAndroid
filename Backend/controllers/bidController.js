import Product from "../models/product.js";
import User from "../models/user.js";
import Bid from "../models/bid.js";

import { sendNotification, notificationContent } from "../firebase-config.js";

// BID
export const placeBid = async (req, res) => {
  try {
    const user = req.user;
    const { product_id, bid_amount } = req.body;
    // find product by id and check if its not the product of the user who is bidding
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
        res.status(500).json({ "message": "You already bade on this product" });
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
              sendNotification(
                seller.notificationToken,
                notificationContent(
                  "New bid",
                  "You have a new bid on your product"
                )
              );

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
