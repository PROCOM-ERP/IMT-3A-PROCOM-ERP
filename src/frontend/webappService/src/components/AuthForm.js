import "../css/App.css";
import "../css/AuthForm.css";
import React from 'react';
import { useNavigate } from 'react-router-dom';
import Button from "./Button";

function Form() {
    // React State
    const navigate = useNavigate();
    const [errorMessage, setErrorMessage] = React.useState({});
    const [values, setValues] = React.useState({
        username: "",
        password: "",
        showPassword: false,
    });

    // User login info
    const database = [
        {
          username: "user1",
          password: "pass1"
        },
        {
          username: "user2",
          password: "pass2"
        }
    ];

    // User login error
    const errors = {
        usernameError: "Invalid username",
        passwordError: "Invalid password"
    };

    // Generate JSX code for error message
    const renderErrorMessage = (name) =>
    name === errorMessage.name && (
        <div className="error">{errorMessage.message}</div>
    );
    
    const handleClickShowPassword = (event) => {
        event.preventDefault();
        setValues({
            ...values,
            showPassword: !values.showPassword,
        });
    };
 
    const handleMouseDownPassword = (event) => {
        event.preventDefault();
    };
 
    const handleValueChange = (prop) => (event) => {
        setValues({
            ...values,
            [prop]: event.target.value,
        });
    };

    const handleReset = () => {
        setValues({
            ...values,
            "username" : "",
            "password" : "",
        });
        setErrorMessage({});
    };

    function handleSubmit(event) {
        // Prevent the browser from reloading the page
        event.preventDefault();

        // Find user login info
        // TODO : demander à Thibaut get user en fonction du username (est-ce que le username existe ou que l'email?)
        const userData = database.find((user) => user.username === values.username);

        // Compare user info
        // TODO : demander à Thibaut comparaison password pour valider la connexion (API)
        if (userData) {
            if (userData.password !== values.password) {
                // Invalid password
                setErrorMessage({ name: "passwordError", message: errors.passwordError });
            } else {
                navigate('/home');
            }
        } else {
            // Username not found
            setErrorMessage({ name: "usernameError", message: errors.usernameError });
        }
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
                <input className=""
                        type="text"
                        name="username"
                        onChange={handleValueChange("username")}
                        value={values.username}
                    />
                {renderErrorMessage("usernameError")}
                </div>

                <br/>
                
                <div className='passwordInput'>
                <label>
                    Password :
                </label>
                <input 
                        type={values.showPassword ? "text" : "password"}
                        name="password"
                        onChange={handleValueChange("password")}
                        value={values.password}
                    />
                <Button onClick={handleClickShowPassword} onMouseDown={handleMouseDownPassword} >   
                    {values.showPassword ? "O" : "X" } 
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
