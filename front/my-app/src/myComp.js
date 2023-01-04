

function Label(params){
    console.log(params)
    return (<div style={{color: params.color}}> My test instructions {params.label}</div>)
}

export default Label;