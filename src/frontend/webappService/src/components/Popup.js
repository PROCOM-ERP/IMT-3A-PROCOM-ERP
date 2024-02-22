import React from 'react';
import Button from './Button';
import "../css/Popup.css";

function Popup({ title, content, onClose }) {
  return (
    <div className="popup">
      <div className="popup-content">
        <h2>{title}</h2>
        <p>{content}</p>
        <Button onClick={onClose}>OK</Button>
      </div>
    </div>
  );
}

export default Popup
