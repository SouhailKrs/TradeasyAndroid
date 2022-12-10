import { body } from "express-validator";
import Category from "../models/category.js";
import Product from "../models/product.js";
import User from "../models/user.js";

// ADD PRODUCT

export const addProduct = async (req, res) => {
  try {
    var {
      category,
      name,
      description,
      price,
      image,
      quantity,
      for_bid,
      bid_end_date,
      bid_event,
      start_bid,
    } = req.body;
    const user = await User.findById(req.user._id);
    const cat = await Category.findOne({ name: category });
    if (!cat) {
      return res.status(400).json({ message: "please select a category" });
    } else {
      (async () => {
        if (bid_end_date == "1m") {
          bid_end_date = new Date().getTime() + 60000;
        } else if (bid_end_date == "1h") {
          bid_end_date = new Date().getTime() + 3600000;
        } else if (bid_end_date == "1d") {
          bid_end_date = new Date().getTime() + 86400000;
        }
        
        else if (start_bid == "1m") {
          start_bid = new Date().getTime() + 60000;
        } else if (start_bid == "2m") {
          start_bid = new Date().getTime() + 120000;
        }
        else if(start_bid == "1d"){
          start_bid = new Date().getTime() + 86400000;
        } else if(start_bid == "1w"){
          start_bid = new Date().getTime() + 604800000;
        }


        const newProduct = new Product({
          user_id: user._id,
          name,
          description,
          price,
          image: image,
          category: category,
          quantity,
          added_date: new Date().getTime(),
          bid_end_date: bid_end_date,
          for_bid: for_bid,
          bid_event: bid_event,
          start_bid: start_bid,
        });
        await newProduct.save().then((product) => {
          res.json({ data: product });
        });
      })();
    }
  } catch (error) {
    res.status(500).json(error.message);
  }
};

// GET PRODUCT OF CONNECTED USER
export const getProducts = async (req, res) => {
  try {
    const user = await User.findById(req.user._id);
    res.json(user.products);
  } catch (err) {
    return res.status(500).json({ msg: err.message });
  }
};

// GET PRODUCT BY SEARCH OF USERNAME
export const userProducts = async (req, res) => {
  const { username } = req.body;
  const user = await User.findOne({ username: username });

  try {
    if (!user) {
      return res.status(400).json({ msg: "please enter a username" });
    } else {
      res.json(user.products);
    }
  } catch (err) {
    return res.status(500).json({ msg: err.message });
  }
};

// GET ALL PRODUCTS
export const getAllProducts = async (req, res) => {
  try {
    const products = await Product.find();
    res.json(products);
  } catch (err) {
    return res.status(500).json({ msg: err.message });
  }
};

// FLITER PRODUCTS BY CATEGORY
export const filterProducts = async (req, res) => {
  const { category } = req.body;

  try {
    const allProds = await User.aggregate([
      { $unwind: "$products" },
      { $match: { "products.category": category } },
      { $group: { _id: "$products" } },
    ]);

    res.json({ allProds });
  } catch (err) {
    return res.status(500).json({ msg: err.message });
  }
};

//UPDATE PRODUCT
export const updateProduct = async (req, res) => {
  try {
    const { name, description, price, image, quantity } = req.body;
    await Product.findOneAndUpdate(
      { _id: req.params.id },
      {
        name,
        description,
        price,
        image,
        quantity,
      }
    );
    res.json({ msg: "Updated a Product" });
  } catch (err) {
    return res.status(500).json({ msg: err.message });
  }
};

// GET PRODUCTS THAT ARE FOR BID
export const getProductsForBid = async (req, res) => {
  try {
    const products = await Product.find({ for_bid: true });
    res.send({ data: products });
  } catch (err) {
    return res.status(500).json({ msg: err.message });
  }
};
// add product with upload image
