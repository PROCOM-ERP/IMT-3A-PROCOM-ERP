import "../css/App.css";
import "../css/AuthForm.css";
import React, {useState} from 'react';
import { useNavigate } from 'react-router-dom';
import Button from "./Button";

function Form() {
    // React State
    const navigate = useNavigate();
    const [errorMessage, setErrorMessage] = React.useState({});
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

    // Encodez les identifiants en Base64
    const base64Credentials = btoa(`${user.username}:${user.password}`);

    // Préparez l'en-tête 'Authorization' avec la valeur 'Basic' suivie de la chaîne encodée en Base64
    const headers = {
        'Authorization': `Basic ${base64Credentials}`,
    };

    function handleSubmit(event) {
        // Prevent the browser from reloading the page
        event.preventDefault();

        // Find user login info
        // TODO : demander à Thibaut get user en fonction du username (est-ce que le username existe ou que l'email?)
        //const userData = database.find((user) => user.username === user.username);

        // Compare user info
        // TODO : demander à Thibaut comparaison password pour valider la connexion (API)
        //if (userData) {
        //    if (userData.password !== user.password) {
        //        // Invalid password
        //        setErrorMessage({ name: "passwordError", message: errors.passwordError });
        //    } else {
        //        navigate('/home');
        //    }
        //} else {
            // Username not found
        //    setErrorMessage({ name: "usernameError", message: errors.usernameError });

        // Define the API URL
        const apiUrl = "https://localhost:8041/api/auth/v1/jwt";

        // Make the API request
        fetch(apiUrl, {
        method: "POST",
        credentials: "include",
        headers: headers,
        })
        .then((response) => {
            if (!response.ok) throw new Error(response.status);
            response.json();
        })
        .then(data => {
            console.log(data);
            // Traitement des données de la réponse ici
        })
        .catch(error => {
            setErrorMessage({ name: "credentialsError", message: errors.credentialsError });
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
                {renderErrorMessage("usernameError")}
                </div>

                <br/>
                
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
                {renderErrorMessage("passwordError")}
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
