import { initializeApp } from "firebase/app";
import "../styles/App.css";
import Maps from "./Maps";
import AuthRoute from "./auth/AuthRoute";

// REMEMBER TO PUT YOUR API KEY IN A FOLDER THAT IS GITIGNORED!!
// (for instance, /src/private/api_key.tsx)
// import {API_KEY} from "./private/api_key"

const firebaseConfig = {
  apiKey: process.env.API_KEY,
  authDomain: process.env.AUTH_DOMAIN,
  projectId: process.env.PROJECT_ID,
  storageBucket: process.env.STORAGE_BUCKET,
  messagingSenderId: process.env.MESSAGING_SENDER_ID,
  appId: process.env.APP_ID,
};

/**
 * Initializes the Firebase app with the configuration provided.
 * @param {object} firebaseConfig - The Firebase configuration object.
 */
initializeApp(firebaseConfig);

/**
 * The main component of the application.
 * @returns {JSX.Element} The rendered JSX element representing the entire application.
 */
function App() {
  return (
    <div className="App">
      {/* Authenticated route component rendering MapsGearup component */}
      <AuthRoute gatedContent={<Maps />} />
    </div>
  );
}

export default App;