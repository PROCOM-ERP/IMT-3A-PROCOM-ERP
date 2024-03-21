import "../css/Button.css"
import React from 'react';

function Button({ user = "user", type, value, onClick, children, ...rest }) {
    return (
        <>
            <button className={`${user}`} type={type} value={value} onClick={onClick} {...rest}>{children}</button>
        </>
    )
}

export default Button
