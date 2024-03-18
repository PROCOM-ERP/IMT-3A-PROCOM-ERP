import '../css/App.css'; // Import CSS file for styling
import React from 'react'; // Import React library
import AuthForm from '../components/AuthForm.js'; // Import authentication form component for user authentication

// Functional component for authentication page
function Authentication() {
  // Render the Authentication component
  return (
    <>
      {/* Render the AuthForm component */}
      <AuthForm />
    </>
  );
}
export default Authentication;
