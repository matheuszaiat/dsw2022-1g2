<template lang="html">
  <div class="bloco-descricao" v-if="this.$root.credentials">
    <div class="item-descricao"></div>
    <h1>Descrição do Item</h1>
    <ul>
      <li>Nome: {{ item.nome }}</li>
      <li>Descrição: {{ item.descricao }}</li>
      <li>Tipo do item: {{ item.tipo }}</li>
      <button @click="compartilhador()">Compartilhar</button>

      <div v-if="compartilhar">
      <form @submit.prevent="postarCompartilhamento()">
        <div class="form-group">
          <label >Email do usuário</label>
          <input type="text" class="form-control" placeholder="Entre o email do usuário que receberá o item" v-model="formulario.email">
          <span class="error" ></span>
        </div>

        <div class="form-group">
          <div>
            <label for="descricao">Data de início</label>
            <input type="date" v-model="formulario.dataInicio">
          </div>
          <div>
            <label for="descricao">Data de término</label>
            <input type="date" v-model="formulario.dataTermino">
          </div>
        </div>

        <button type="submit" class="btn btn-primary">Envia</button>
      </form>
      </div>
      <div v-for= "uso in this.compartilhamentos">
        <div class="usos-item">
          <h4>Usos: {{uso.id}}</h4>
          <li>data de início: {{ uso.dataInicio }}</li>
          <li>data de término: {{ uso.dataTermino }}</li>
          <li>nome do usuário que receberá o item: {{ uso.nomeUsuario }}</li>
          <li>Status do compartilhamento: {{ uso.status }}</li>
          <button @click="removerCompartilhamentos(uso.id)">Cancelar compartilhamento</button>
          
      </div>
    </div>
      
    </ul>
    
     
  </div>

  
</template>
<script>
  import axios from 'axios';
  
  export default {
    props: ['item'],
    
    data() {
      return {
        page: 1,
        compartilhar: false,
        formulario: { 
          email: "",
          item: this.item.nome,
          dataInicio: "",
          dataTermino: ""
        },
        compartilhamentos: {
        },
        error: {
        },
        httpOptions: {
          baseURL: this.$root.config.url,
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + this.$root.credentials.token
          }
        },
      }
    },

   created: function () {
    console.log("AQUIIIIIIIIIII   ", this.compartilhar);
    this.getCompartilhamentos();
    
  },
  methods: {

    goBackToList: function() {
      this.$router.replace('/item/list');
    },
    compartilhador: function() {
      this.compartilhar = !this.compartilhar;
    },
    postarCompartilhamento: function(){
      console.log(this.formulario);
      axios.post("/api/compartilhamento/novo", this.formulario, this.httpOptions)
        .then(response => {
          this.error = {};
          console.log("SUCESSOOOOOO");
          this.goBackToList;
          //setTimeout(this.goBackToList, 3000);
        })
        .catch(error => {
          this.error = error.response.data.errors;
          console.log(this.error);
        });
    },
    getCompartilhamentos: function(){
      axios.get("/api/compartilhamento/usos/?itemId=" + this.item.id + "&page=" + this.page + "&sort=&per_page=10", this.httpOptions)
        .then(response => {
          //this.success = true;
          this.page = response.data.data.current_page;
          this.compartilhamentos = response.data.data.data;
          this.error = {};
          
          //setTimeout(this.goBackToList, 3000);
        })
        .catch(error => {
          this.error = error.response.data.errors;
        });
    },
    removerCompartilhamentos: function(compartilhamentoId){
      axios.delete("/api/compartilhamento/" + compartilhamentoId, this.httpOptions)
        .then(response => {
          //this.success = true;
          console.log("Cancelou!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
          this.error = {};
          //setTimeout(this.goBackToList, 3000);
        })
        .catch(error => {
          this.error = error.response.data.errors;
        });
    }

  }
}
</script>
<style>

.bloco-descricao{
    margin-left: auto;
    margin-right: auto;
    display: block;
    width: 500px;
    height: 1000px;
    background-color: gray;
    border-radius: 2px;
}

.usos-item{
  margin-top: 20px;
}
</style>