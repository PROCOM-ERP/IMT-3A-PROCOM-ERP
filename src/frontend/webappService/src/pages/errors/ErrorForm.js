import React from 'react';
import Popup from '../../components/Popup.js';

function ErrorForm({ title, message }) {
  const [showPopup, setShowPopup] = useState(true);

  const closePopup = () => {
    setShowPopup(false);
  };

  return (
    <>
      {showPopup && (
        <Popup
          title={title}
          content={message}
          onClose={closePopup}
        />
      )}
    </>
  )
}

export default ErrorForm
