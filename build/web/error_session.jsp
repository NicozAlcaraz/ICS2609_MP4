<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<c:set var="error" value="${(empty error) ? 'An unspecified error has occurred, or you accessed this error page intentionally. In this case, welcome!' : error}" />
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Open+Sans:ital,wght@0,300;0,400;0,500;0,600;0,700;0,800;1,300;1,400;1,500;1,600;1,700;1,800&display=swap" rel="stylesheet">
        <style>
html {
    box-sizing: border-box;
}

body {
    background: white;
    font-family: "Open Sans", sans-serif;
    margin: 0;
}

/* HEADER */

header {
    width: 100%;
    position: fixed;
    top: 0;
}

.header1 {
    width: 100%;
    height: 50px;
    background-color: rgba(0,0,0,0.9);
    padding-left: 20vw;
    font-size: 1.1em;
    font-weight: bold;
    color: #FFFFFF;
    display: flex;
    align-items: center;
    top: 0;
    border-bottom: 1px solid rgba(0,0,0,0.9);
    box-shadow: inset 0px -3px 15px 0px rgba(0,0,0,0.5);
}

/* MAIN BODY */

main {
    padding-top: 100px;
    display: block;
    width: 40%;
    max-width: 800px;
    margin: 0 auto 10px auto;
}

h1 {
    font-size: 2.4rem;
    margin-left: 0.5rem;
    margin-bottom: 0.5rem;
}

/* <div> BOX SETTINGS */

.box {
    border: 2px solid #DBDBDB;
    box-shadow: 0px 5px 10px 0px rgba(0,0,0,0.2);
    padding: 50px 55px 60px;
    display: block;
}

p, legend {
    margin: auto auto 0.8rem;
}

legend {
    font-size: 0.8rem;
}

fieldset {
    margin: 0;
    padding: 0;
    font-weight: bold;
}

form {
    display: block;
    font-size: 1rem;
    color: #444444;
}

input {
    display: block;
    margin: 0.5rem 0;
    width: 100%;
    font-weight: normal;
}

.result {
    font-style: italic;
    font-weight: normal;
    font-size: 1em;
    text-align: center;
}

input[type="submit"], button {
    width: fit-content;
    height: 2em;
    background-color: rgba(0,0,0,0.9);
    background-image: linear-gradient(to top, rgba(0,0,0,0.3), rgba(0,0,0,0) 50%);
    box-shadow: 0px 0.1em 6px 0px rgba(0,0,0,0.3);
    color: #FFFFFF;
    font-size: 0.9em;
    border: 1px solid rgba(0,0,0,0.7);
    border-radius: 0px;
    font-weight: bold;
}

fieldset {
    display: block;
    border: 0px;
}

/* FOOTER */

footer {
    text-align: center;
    display: block;
    font-size: 0.8rem;
    color: #111111;
    margin-top: 1.5vw;
}

/* RESPONSIVE SETTINGS */

@media (max-width: 908px) {
    .header1 {
        padding-left: 1.3em;
        padding-right: auto;
        font-size: 1.3em;
    }
    
    main {
        width: 100vw;
        max-width: none;
        border-left: none;
        border-right: none;
    }
    
    .header1, h1 {
        text-align: center;
    }
    
    legend, label {
        font-size: 1.2em;
    }
    
    input {
        height: 2em;
    }
    
    input[type="submit"], button {
        width: 100%;
        height: 2.2em;
        font-size: 1.2em;
    }
}
</style>
        <title>Error</title>
    </head>
    <body>
        <header>
            <div class="header1">
                <% out.print(getServletContext().getInitParameter("header")); %>
            </div>
        </header>
        <main>
            <div class="box">
                <p>Oops! it seems like you're in the wrong page, please try again.</p>
                <a href="./">
                    <button>Back</button>
                </a>
            </div>
        </main>
        <footer>
            <% out.print(getServletContext().getAttribute("date")); %><br>
            <% out.print(getServletContext().getInitParameter("footer")); %>
        </footer>
    </body>
</html>

