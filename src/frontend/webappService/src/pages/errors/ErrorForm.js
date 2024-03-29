import React, { useState } from "react";
import Popup from "../../components/Popup.js";

function ErrorForm({ title, message, onClose }) {
  return <>{<Popup title={title} content={message} onClose={onClose} />}</>;
}

export default ErrorForm;
