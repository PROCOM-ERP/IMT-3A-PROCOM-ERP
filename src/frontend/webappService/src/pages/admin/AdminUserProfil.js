import React, { useEffect } from 'react'; // Import React library
import { useNavigate, useParams } from 'react-router-dom'; // Import useParams hook from react-router-dom
import CheckAdminConnection from '../../components/CheckAdminConnection'; // Import CheckAdminConnection component
import Navbar from '../../components/Navbar'; // Import Navbar component
import UserProfilAdmin from '../../components/admin/UserProfilAdmin'; // Import UserProfilAdmin component

function AdminUserProfil() {

  const { userId } = useParams(); // Get the userId parameter from the URL using useParams hook
  const navigate = useNavigate(); // Hook to navigate programmatically

  // useEffect hook to check if userId format is correct
  useEffect(() => {
    const isValidUserId = /^A\d{5}$/.test(userId); // Regular expression to check if userId starts with 'A' followed by exactly 5 digits
    // Redirect to error 403 page if userId format is not valid
    if (!isValidUserId) {
      navigate("/error403");
    }
  }, [userId, navigate]); // Dependencies for useEffect: userId and navigate

  // Render the AdminUser component
  return (
    <>
      {/* Render the CheckAdminConnection component to check jwt exists and if user is admin */}
      <CheckAdminConnection />
      {/* Render the Navbar component with the prop navUser='admin' to manage navbar content and color */}
      <Navbar navUser='admin' />
      {/* Render the UserProfilAdmin component with the title "User Profil" and the userId obtained from the URL */}
      <UserProfilAdmin title="User Profil" userId={userId} />
    </>
  );
}
export default AdminUserProfil;
