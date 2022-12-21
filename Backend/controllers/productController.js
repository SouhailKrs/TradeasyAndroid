import Category from "../models/category.js";
import Product from "../models/product.js";
import User from "../models/user.js";

// ADD PRODUCT

export const addProduct = async (req, res) => {
  try {
    let bid_end_date = req.body.bid_end_date;

    const cat = await Category.findOne({ name: req.body.category });
    if (!cat) {
      return res.status(400).json({ "message": "please select a category" });
    } else {
      await (async () => {
        if (bid_end_date === "1m") {
          bid_end_date = new Date().getTime() + 6000;
        } else if (bid_end_date === "1h") {
          bid_end_date = new Date().getTime() + 3600000;
        } else if (bid_end_date === "1d") {
          bid_end_date = new Date().getTime() + 86400000;
        }
        const newProduct = new Product({
          user_id: req.user._id,
          name: req.body.name,
          description: req.body.description,
          price: req.body.price,
          image: `${req.protocol}://${req.get("host")}/img/${
            req.file.filename
          }`,
          category: req.body.category,
          quantity: req.body.quantity,
          added_date: new Date().getTime(),
          bid_end_date: bid_end_date,
          for_bid: req.body.for_bid,
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
export const getUserProducts = async (req, res) => {
  try {
    // search user products in product collection
    const products = await Product.find({ user_id: req.user._id });
    res.json({ data: products });
  } catch (err) {
    return res.status(500).json({ "message": err.message });
  }
};

// GET PRODUCT BY SEARCH OF USERNAME
export const usernameProducts = async (req, res) => {
  try {
    const { username } = req.body;
    const user = await User.findOne({ username: username });

    if (!user) {
      return res.status(400).json({ "message": "user not found" });
    } else {
      const products = await Product.find({ user_id: user._id });
      res.json({ data: products });
    }
  } catch (err) {
    return res.status(500).json({ "message": err.message });
  }
};

// GET ALL PRODUCTS
export const getAllProducts = async (req, res) => {
  try {
    const products = await Product.find();
    res.json(products);
  } catch (err) {
    return res.status(500).json({ "msg": err.message });
  }
};

// FILTER PRODUCTS BY CATEGORY
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
    return res.status(500).json({ "message": err.message });
  }
};

// GET PRODUCTS   FOR BID
export const getProductsForBid = async (req, res) => {
  try {
    const products = await Product.find({
      for_bid: true,
      user_id: { $ne: req.user._id },
    });
    res.send({ data: products });
  } catch (err) {
    return res.status(500).json({ "message": err.message });
  }
};

// fin product by name and return empty array if not found
export const findProductByName = async (req, res) => {
  const { name } = req.body;
  // check if name is empty
  try {
    const products = await Product.find({
      name: { $regex: "^" + name, $options: "i" },
    }).limit(10);
    // return empty array if not found and name is empty
    if (products.length === 0 || name === "") {
      res.send({ data: [] });
    } else {
      res.send({ data: products });
    }
  } catch (err) {
    return res.status(500).json({ "message": err.message });
  }
};

// add product to saved items
export const addProdToSaved = async (req, res) => {
  try {
    const { product_id } = req.body;

    const product = await Product.findOne({ _id: product_id });
    // check if product already exists in saved items
    const user = req.user;
    const savedProducts = user.savedProducts;
    if (savedProducts.length > 0) {
      const found = savedProducts.some((el) => el._id === product_id);

      if (found) {
        return res.status(400).json({ "message": "product already exists" });
      } else {
        user.savedProducts.push(product);
        await user.save();
        res.send({ data: user });
      }
    } else {
      user.savedProducts.push(product);
      await user.save();
      res.send({ data: user });
    }
  } catch (err) {
    return res.status(500).json({ "message": err.message });
  }
};
// get saved products
export const getSavedProds = async (req, res) => {
  try {
    const user = await req.user;
    res.send({ data: user.savedProducts });
  } catch (err) {
    return res.status(500).json({ "message": err.message });
  }
};

// find products  were sold is false of connected user
export const userSelling = async (req, res) => {
  try {
    const products = await Product.find({ user_id: req.user._id, sold: false });
    res.send({ data: products });
  } catch (err) {
    return res.status(500).json({ "message": err.message });
  }
};

// buy now
export const buyNow = async (req, res) => {
  try {
    const { id } = req.body;
    const product = await Product.findById(id);
    if (!product) {
      return res.status(400).json({ "msg": "product not found" });
    }
    if (product.sold === true) {
      return res.status(400).json({ "msg": "product already sold" });
    } else {
      await Product.findOneAndUpdate(id, {
        $set: {
          sold: true,
        },
      });
      await User.findOneAndUpdate(
        { _id: req.user._id },
        {
          $push: {
            products: product,
          },
        }
      );
      res.json({ "msg": "product bought" });
    }
  } catch (err) {
    return res.status(500).json({ "msg": err.message });
  }
};
