import admin from "firebase-admin";
import serviceAccount from "./serviceAccountKey.json" assert {type: "json"};

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
});

export const notificationContent=(title,body)=>{
    const message = {
        notification: {
            title: title,
            body: body,
        },
    };
    return message
}




  // a function to send a notification to a user that takes token and message as parameters
   export  const sendNotification = (registrationToken, message) => {
        admin.messaging().sendToDevice(registrationToken, message)
            .then((response) => {
                console.log('Successfully sent message:', response);
            })
            .catch((error) => {
                console.log('Error sending message:', error);
            });
    }