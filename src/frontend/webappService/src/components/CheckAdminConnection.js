import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';


function CheckAdminConnection() {
  const navigate = useNavigate();

  // Check if a user is connected
  const checkIsConnected = () => {
    return !!localStorage.getItem('Token');
  }

  // Check if user is admin
  const checkIsAdmin = async () => {
    const token = localStorage.getItem("Token");
    const headers = {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    };
    const apiUrl = `https://localhost:8041/api/authentication/v1/roles`;

    try {
      const response = await fetch(apiUrl, {
        method: "GET",
        headers: headers,
      });

      if (!response.ok) {
        console.error(`Error: ${response.status}`);
        return false;
      }
      else {
        return true;
      }
    } catch (error) {
      console.error("Error:", error);
      return false;
    }
  }

  useEffect(() => {
    const userConnected = checkIsConnected();
    if (!userConnected) {
      navigate("/");
    } else {
      // Check if user is an admin
      checkIsAdmin()
        .then(result => {
          if (!result) {
            navigate("/home");
          }
        })
        .catch(error => {
          console.error("Error:", error);
          navigate("/home");
        });
    }
  }, [navigate]);

  return null; // Or you can return a loading indicator or placeholder
}

export default CheckAdminConnection
