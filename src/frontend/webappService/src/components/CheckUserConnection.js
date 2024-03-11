import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

function CheckUserConnection() {
  const navigate = useNavigate();

  // Check if a user is connected
  const checkIsConnected = () => {
    return !!localStorage.getItem('Token');
  }

  useEffect(() => {
    const userConnected = checkIsConnected();
    if (!userConnected) navigate("/");
  }, [navigate]);

  return null; // Or you can return a loading indicator or placeholder
}

export default CheckUserConnection;
