import './Header.css';

export const Header = ({headerImage}) => {
          
    return(
        <div style={{backgroundImage: headerImage}} id="header-container" className="header-container"></div>
    )
}