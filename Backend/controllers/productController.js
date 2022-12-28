import Category from "../models/category.js";
import Product from "../models/product.js";
import User from "../models/user.js";

// ADD PRODUCT

export const addProduct = async (req, res) => {
  try {
    let bid_end_date = req.body.bid_end_date;
    const cat = await Category.findOne({ name: req.body.category });
    const imagePath = `${req.protocol}://${req.get("host")}/img/`;
    if (!cat) {
      return res.status(400).json({ "message": "please select a category" });
    } else {
      await (async () => {
        if (bid_end_date === "1 Hour") {
          bid_end_date = new Date().getTime() + 3600000;
        } else if (bid_end_date === "1 Day") {
          bid_end_date = new Date().getTime() + 86400000;
        } else if (bid_end_date === "1 Week") {
          bid_end_date = new Date().getTime() + 86400000 * 7;
        }

        const newProduct = new Product({
          user_id: req.user._id,
          name: req.body.name,
          description: req.body.description,
          price: req.body.price,
          image:
            // add image path for each image
            req.files.map((file) => {
              return imagePath + file.filename;
            }),

          category: req.body.category,
          quantity: req.body.quantity,
          added_date: new Date().getTime(),
          bid_end_date: bid_end_date,
          for_bid: req.body.for_bid,
          username: req.user.username,
          userPhoneNumber: req.user.phoneNumber,
          userProfilePicture: req.user.profilePicture,
          selling:true,
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
    const products = await Product.find({ user_id: req.user._id ,selling:false });
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

// fin product by name and return empty array if not found
export const findProdudctByName = async (req, res) => {
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
      for (var i = 0; i < savedProducts.length; i++) {

        var alreadyExists = false;
        if (savedProducts[i]._id.toString() == product._id) {
          console.log("product already exists");
          alreadyExists = true;

        }
      }


      if (alreadyExists) {

        return res.status(400).json({ "message": "product already exists" });
      }
      else {
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
    const products = await Product.find({ user_id: req.user._id, selling: true });
    res.send({ data: products });
  } catch (err) {
    return res.status(500).json({ "message": err.message });
  }
};


// fucntion to add product to recently viewed and check if product already exists


// add product to recently viewed
export const recentlyViewed = async (req, res) => {
  try {
    const { product_id } = req.body;

    const product
      = await Product.findOne({ _id: product_id });
    // check if product already exists in saved items
    const user = req.user;
    const recentProducts = user.recentlyViewed;
    if (recentProducts.length > 0) {

      for (var i = 0; i < recentProducts.length; i++) {

        var alreadyExists = false;
        if (recentProducts[i]._id.toString() == product._id) {
          console.log("product already exists");
          alreadyExists = true;

        }
      }


      if (alreadyExists) {

        return res.status(400).json({ "message": "product already exists" });
      } else {
        if (recentProducts.length > 2) {
          user.recentlyViewed.shift();
          user.recentlyViewed.push(product);
          await user.save();
          res.send({ data: user });
        }
        else {
          user.recentlyViewed.push(product);
          await user.save();
          res.send({ data: user });
        }
      }

    }
    else {
      user.recentlyViewed.push(product);
      await user.save();
      res.send({ data: user });
    }
  } catch (err) {
    return res.status(500).json({ "message": err.message });
  }
};



// get products by category limit 10 except of the connected user and if user in not connected get all products
export const getProductsByCategory = async (req, res) => {
  try {
    const { category } = req.body;
    if (req.user) {
      const products = await Product.find
        ({
          category
            : category, selling:true , sold:false , user_id: { $ne: req.user._id}
        }).limit(10);
     
      if (products.length === 0) {
        res.status(500).json({ "message": "No products yet in this category" });
      }
      else {
  
        res.send({ data: products });
      }
    } else {
    
      const products = await Product.find
        ({
          category
            : category
        }).limit(10);
      if (products.length === 0) {
        res.status(500).json({ "message": "No products yet in this category" });
      }
      else {

        res.send({ data: products });
      }
    }
  } catch (err) {
    return res.status(500).json({ "message": err.message });
  }
};

// get products by for bid  limit 10 except of the connected user and if user in not connected get all products for bid 
export const getProductsForBid = async (req, res) => {
  try {
    if (req.user) {
      const products = await Product.find
        ({
          forBid: true,  user_id: { $ne: req.user._id }
        }).limit(10);
      if (products.length === 0) {
        res.status(500).json({ "message": "No products yet for bid" });
      }
      else {
        res.send({ data: products });
      }
    } else {
      const products = await Product.find
        ({
          forBid: true
        }).limit(10);
      if (products.length === 0) {
        res.status(500).json({ "message": "No products yet for bid" });
      }
      else {
        res.send({ data: products });
      }
    }
  } catch (err) {
    return res.status(500).json({ "message": err.message });
  }
}


// search products by name if user is connected get all products except of the connected user else get all products
export const findProductByName = async (req, res) => {
  try {
    const { name } = req.body;
    if (req.user) {
      const products = await Product.find
        ({
          name: { $regex: "^" + name, $options: "i"  }, selling:true , sold:false , user_id: { $ne: req.user._id}
        });
      if (products.length === 0) {
        res.status(500).json({ "message": "No products yet"  });
      }
      else {
        res.send({ data: products });
      }
    } else {
      const products = await Product.find
        ({
          name: { $regex: "^" + name, $options: "i"  }
        });
      if (products.length === 0) {
        res.status(500).json({ "message": "No products yet" });
      }
      else {
        res.send({ data: products });
      }
    }
  } catch (err) {
    return res.status(500).json({ "message": err.message });
  }
};

// remove product from selling
export const removeProductFromSelling = async (req, res) => {
  try {
    // search for product of the connected user


    const { product_id } = req.body;
    const product = await Product.findOne({ _id: product_id, user_id: req.user._id });
    if (product) {
      product.selling = false;
      await product.save();
      res.send({ data: product });
    }
    else {
      res.status(500).json({ "message": "product not found" });
    }
  } catch (err) {
    return res.status(500).json({ "message": err.message });
  }
};


// delete product and check if product is in saved items or recently viewed and remove it
export const deleteProduct = async (req, res) => {
  try {
    const { product_id } = req.body;
    const product
      = await
        Product
          .
            findOne({ _id: product_id, user_id: req.user._id });
    if (product) {
      // check if product is in saved items
      const user = req.user;
      const savedProducts = user.savedItems;
      if (savedProducts.length > 0) {
        for (var i = 0; i < savedProducts.length; i++) {
          if (savedProducts[i]._id.toString() == product._id) {
            savedProducts.splice(i, 1);
            await user.save();
          }
        }
      }
      // check if product is in recently viewed
      const recentProducts = user.recentlyViewed;
      if (recentProducts.length > 0) {
        for (var i = 0; i < recentProducts.length; i++) {
          if (recentProducts[i]._id.toString() == product._id) {
            recentProducts.splice(i, 1);
            await user.save();
          }
        }
      }
      await product.remove();
      res.send({ data: product });
    }
    else {
      res.status(500).json({ "message": "product not found" });
    }
  } catch (err) {
    return res.status(500).json({ "message": err.message });
  }
};
