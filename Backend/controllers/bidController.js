
import Product from "../models/product.js";
import User from "../models/user.js";
import bid from "../models/bid.js";
import product from "../models/product.js";
import websocket from "ws";
import { set } from "mongoose";




// BID



export const placeBid = async (req, res) => {

    try {
        const { product_id, bid_amount } = req.body;
        const user = await User.findById(req.user._id);

        var timer;

        Product.findById(product_id).then((p) => {
            var bidTimer2 = new Date().getTime() - p.start_bid;
            var seconds = Math.floor((bidTimer2 % (1000 * 60)) / 1000);

            


            timer = setInterval(function () {
                
                var bidTimer2 = ((p.start_bid + 10000) - new Date().getTime());
                var seconds2 = Math.floor((bidTimer2 % (1000 * 60)) / 1000);
                console.log(seconds2);
                if (seconds2 == 0) {
                    p.for_bid = false;
                    p.save();
                    clearInterval(timer);
                }

            }, 1000);

            if (p.for_bid == true) {
                if (seconds < 1000) {
                    const newBid = new bid({
                        user_id: user,
                        product_id: p,
                        bid_amount: bid_amount,
                    });
                    //update product price 

                    p.bidded = true;
                    p.price = bid_amount;
                    p.start_bid = new Date().getTime();
                    p.save();
                    newBid.save();
                    res.json({ data: newBid });



                } else if (p.price > bid_amount) {
                    res.status(400).json({ message: "bid amount must be greater than current price" });
                } else if (seconds > 1000) {
                    res.status(400).json({ message: "bid has ended" });
                    p.for_bid = false;
                    p.save();
                }


            }


            else {
                res.status(400).json({ message: "this product is not for bid or bid didn't start yet" });
            }


            //clearInterval(timer);
        });
    } catch (error) {
        res.status(400).json({ message: "error" });
    }

};





