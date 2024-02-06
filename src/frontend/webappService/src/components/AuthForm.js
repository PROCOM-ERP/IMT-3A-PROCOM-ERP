import "../css/App.css";
import "../css/AuthForm.css";
import React, {useState} from 'react';
import { useNavigate } from 'react-router-dom';
import Button from "./Button";

function Form() {
    // React State
    const navigate = useNavigate();
    const tokenName = "Token";
    const [errorMessage, setErrorMessage] = useState({});
    const [user, setUser] = useState({
        username: "",
        password: "",
        showPassword: false,
    });

    // User login error
    const errors = {
        credentialsError: "Invalid credentials"
    };

    // Generate JSX code for error message
    const renderErrorMessage = (name) =>
    name === errorMessage.name && (
        <div className="error">{errorMessage.message}</div>
    );

    const handleClickShowPassword = (event) => {
        event.preventDefault();
        setUser({
            ...user,
            showPassword: !user.showPassword,
        });
    };
 
    const handleMouseDownPassword = (event) => {
        event.preventDefault();
    };
 
    const handleValueChange = (prop) => (event) => {
        setUser({
            ...user,
            [prop]: event.target.value,
        });
    };

    const handleReset = () => {
        setUser({
            ...user,
            "username" : "",
            "password" : "",
        });
        setErrorMessage({});
    };

    // Encode identifiers in Base64
    const base64Credentials = btoa(`${user.username}:${user.password}`);

    // Prepare the 'Authorization' header with the value 'Basic' followed by the Base64-encoded string
    const headers = {
        'Authorization': `Basic ${base64Credentials}`,
    };

    function handleSubmit(event) {
        // Prevent the browser from reloading the page
        event.preventDefault();

        // API URL
        const apiUrl = "https://localhost:8041/api/auth/v1/auth/jwt";

        // Make the API request
        fetch(apiUrl, {
        method: "POST",
        credentials: "include",
        headers: headers,
        })
        .then((response) => {
            if (!response.ok) throw new Error(response.status);
            const res = response.text();
            return res;
        })
        .then(data => {
            localStorage.setItem(tokenName, data); // Token stored in local storage
            console.log("[LOG] JWT retrieved and stored");
            navigate("/home"); // Navigate to home page
        })
        .catch(error => {
            setErrorMessage({ name: "credentialsError", message: errors.credentialsError }); // Set error for user
            console.error('API request error: ', error);
        });
    }
         
    return (
        <>
        <div className="authentification-container">
            <div className="login-title">Login</div>
            <form>
                <div className='usernameInput'>
                <label>
                    Username :
                    
                </label>
                <input
                        type="text"
                        name="username"
                        onChange={handleValueChange("username")}
                        value={user.username}
                    />
                </div>
                
                <div className='passwordInput'>
                <label>
                    Password :
                </label>
                <input 
                        type={user.showPassword ? "text" : "password"}
                        name="password"
                        onChange={handleValueChange("password")}
                        value={user.password}
                    />
                <Button onClick={handleClickShowPassword} onMouseDown={handleMouseDownPassword} >   
                    {user.showPassword ? "O" : "X" } 
                </Button>
                {renderErrorMessage("credentialsError")}
                </div>

                <div className="authentification-btn">
                <Button type="reset" value="Reset" onClick={handleReset}>Reset</Button>
                <Button type="submit" value="Submit" onClick={handleSubmit}>Submit</Button>
                </div>

            </form> 
        </div>
        </>
    )
}

export default Form;
