import React, {useEffect} from 'react';


function DirHelloWorld() {
    useEffect(() => {
        fetch("https://localhost:8041/api/dir/v1/hello", {
            method: "GET",
        })
        .then((response) => {
          if (!response.ok) throw new Error(response.status);
          const res = response.text();
          return res;
        })
        .then((status) => console.log("[LOG] dir api response: ", status))
        .catch((error) => {
            console.error(error);
        });
        
    }, []);
  return (
    <></>
  )
}

export default DirHelloWorld
