export function notFoundError(req, res, next) {
    const err = new Error("Not Found");
    err.status = 404;
    next(err);
};

export function errorHandler(err, req, res, next) {
    const token = req.cookies.accessToken;
    if(token){
        res.status(err.status || 500).json({
            message: err.message,
            token: req.cookies.accessToken,
        });
    }
    else{
        res.status(err.status || 500).json({
            message: err.message,
            token: "none",
        });
    }
    
};