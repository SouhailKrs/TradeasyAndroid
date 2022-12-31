
import Category from "../models/category.js";
import Product from "../models/product.js";






// add category
export const addCategory = async (req, res) => {
    const { name } = req.body;
    try { 
        const category = await Category.findOne({ name });
        if (category) {
            return res.status(400).json({ msg: 'Category already exists' });
        }
        const newCategory = new Category({ name });
        await newCategory.save();
        res.json({ msg: 'Category created' });
    } catch (err) {
        return res.status(500).json({ msg: err.message });
    }
};

// a function that returns a list of all categories names
export const getCategories = async (req, res) => {
    try {
        const categories = await Category.find();
        res.status(200).json({ data: categories });
    } catch (err) {
        return res.status(500).json({ "message": err.message });
    }
}

  






export const editProduct = async (req, res) => {
    try {
      let bid_end_date = req.body.bid_end_date;
      let cat = await Category.findOne({ name: req.body.category });
      console.log("prod idd " + req.body.prod_id)
      console.log(cat)
  
      const imagePath = `${req.protocol}://${req.get("host")}/img/`;
      if (!cat) {
        console.log("category")
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
          console.log("prod id " + req.body.prod_id)
          const product = await Product.findOne({ prod_id: req.body.prod_id, user_id: req.user._id });
          if (product) {
            product.name = req.body.name;
            product.description = req.body.description;
            product.price = req.body.price;
  
            product.image =
              req.files.map((file) => {
                return imagePath + file.filename;
              }),
              product.category = req.body.category,
              product.quantity = req.body.quantity,
              product.added_date = new Date().getTime(),
              product.bid_end_date = bid_end_date,
              product.for_bid = req.body.for_bid,
              product.username = req.user.username,
              product.userPhoneNumber = req.user.phoneNumber,
              product.userProfilePicture = req.user.profilePicture,
              product.selling = true,
              product.updateOne(product).then((product) => {
                res.json({ data: product });
              });
          } else {
            res.status(400).json({ "message": "product not found" });
          }
        })();
      }
    } catch (error) {
      res.status(500).json(error.message);
    }
  }; 





