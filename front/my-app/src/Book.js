import React from 'react'
import './Book.scss'
import bookImg from './book.png'

export default class Book extends React.Component {

    render() {
        return (
            <div className="book">
                <div className="book-image">
                    <img src={bookImg} alt="Book" />
                </div>
                <div>Titre : {this.props.title}</div>
                <div>Catégorie: {this.props.category}</div>
                {this.props.lender&& <div>Préteur: {this.props.lender}</div>}
                {this.props.askDate&&<div>Date d'emprunt: {this.props.askDate}</div>}
                {this.props.closeDate&&<div>Date de fin d'emprunt: {this.props.closeDate}</div>}
            </div>
        )
    }

}