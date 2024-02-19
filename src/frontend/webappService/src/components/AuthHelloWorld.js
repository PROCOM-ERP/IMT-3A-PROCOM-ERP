import React, { useEffect, useState } from "react";

function AuthHelloWorld() {
  const [hello, setHello] = useState(null);
  useEffect(() => {
    fetch("https://localhost:8041/api/authentication/v1/hello", {
      method: "GET",
    })
      .then((response) => {
        if (!response.ok) throw new Error(response.status);
        const res = response.text();
        return res;
      })
      .then((status) => console.log("[LOG] auth api response: ", status))
      .catch((error) => {
        console.error(error);
      });
  }, []);
  return <></>;
}

export default AuthHelloWorld;
