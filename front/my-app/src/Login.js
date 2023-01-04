import React from 'react'
import logo from './logo.jpg'
import {Link, useNavigate} from 'react-router-dom'
import './Login.scss'
import axios from 'axios';
import { AUTH_TOKEN_KEY } from './App';
import SimpleModal from './SimpleModal'

class Login extends React.Component {
    constructor(){
        super();
        this.state={userData:{}, showModal: false}
        this.handleChange = this.handleChange.bind(this)
        this.onSubmit = this.onSubmit.bind(this)
        this.handleCloseModal = this.handleCloseModal.bind(this)
    }

     onSubmit (event){
        event.preventDefault();
        console.log("onSubmit")
        axios.post('/authenticate', {...this.state.userData}).then(
          response=>{
                const bearerToken = response?.headers?.authorization;
                if (bearerToken && bearerToken.slice(0, 7) === 'Bearer ') {
                    const jwt = bearerToken.slice(7, bearerToken.length);
                    sessionStorage.setItem(AUTH_TOKEN_KEY,jwt)
                    console.log(response.data.login)
                    this.props.setUserInfo(response.data.login);
                    this.props.history("/myBooks")
                }
            }).catch(() => {
                this.setState({ showModal: true })
          });
    }
    
    handleChange (event) {
        let currentState = {...this.state.userData};
        currentState[event.target.name]= event.target.value;
        this.setState({userData:{...currentState}})
    }

    handleCloseModal() {
        this.setState({ showModal: false })
    }

    render(){
        const title = "Login incorrect"
        const bodyTxt = "Login ou mot de passe incorrect"
        return (
            <>
            <div className="login-container">
                <div>
                    <div>
                        <img src={logo} alt="logo"></img>
                    </div>
                    <div className="title">Bienvenue dans share book!</div>
                    <div className="form-container">
                        <form onSubmit={this.onSubmit}>
                            <span>Mail:</span>
                            <input type="text" className="form-control" name="email" onChange={this.handleChange}></input>
                            <span>Password:</span>
                            <input type="password" className="form-control" name="password" onChange={this.handleChange}></input>
                            <div>
                                <input type="submit" className="btn btn-primary" value="OK"/>
                            </div>
                        </form> 
                    </div>
                    <div>
                        <Link to="/addUser">S'inscrire</Link>
                    </div>
                </div>
            </div>
            <SimpleModal title={title} bodyTxt={bodyTxt} showModal={this.state.showModal} handleCloseModal={this.handleCloseModal}/>
        </>
        )
    }

}

export default function (props) {
    const history = useNavigate();
    return <Login {...props} history={history} />;
  }