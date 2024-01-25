import React, { useEffect, useState } from 'react';

function AuthHelloWorld() {
    const [hello, setHello] = useState(null);
    useEffect(() => {
        fetch("https://localhost:8041/api/auth/v1/hello", {
            method: "GET",
        })
        .then((response) => response.json())
        .then((data) => {
            setHello(data[0].joke);
            console.log(data);
        })
        .catch((error) => {
            setHello(error);
            console.log(error)});
        
    }, [])
  return (
    <>
    <h2>Authentification - Hello World</h2>
    <p>{hello}</p>
    </>
  )
}

export default AuthHelloWorld
