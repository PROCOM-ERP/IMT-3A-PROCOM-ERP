import { useNavigate } from 'react-router-dom';

const handleFormError = async (response, navigate) => {
  if (!response.ok) {
    if (response.status === 401) {
      navigate("/error401");
    } else if (response.status === 403) {
      navigate("/error403");
    } else if (response.status === 400 || response.status === 422) {
      const data = await response.json();
      let formattedMessage = "";
      for (const [field, content] of Object.entries(data.fields || {})) {
        formattedMessage += `${field} : ${content}\n\n`;
      }
      const error = {
        title: data.message || "An error occurred",
        message: formattedMessage || "No details available",
      };
      return [true, error];
    } else {
      throw new Error(response.status + " " + response.statusText);
    }
  }
  return [false, null]; // No error
};

export default handleFormError;