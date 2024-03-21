import "../css/Button.css"
import React from 'react';

function Button({ type, value, onClick, children, ...rest }) {
    return (
        <>
            <button type={type} value={value} onClick={onClick} {...rest}>{children}</button>
        </>
    )
}

export default Button
