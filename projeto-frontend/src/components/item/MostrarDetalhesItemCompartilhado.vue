<template lang="html">

<!-- Esta página mostra os detalhes do item compartilhado, seus usos/compartilhamentos e a possibilidade
    de criar um novo compartilhamento -->

  <div class="bloco-descricao" v-if="this.$root.credentials">
    <div class="item-descricao"></div>
    <h1 class="form-title">Descrição do Item</h1>
    <ul>
      <li>Nome: {{ item.nome }}</li>
      <li>Descrição: {{ item.descricao }}</li>
      <li>Tipo do item: {{ item.tipo }}</li>
      <button class="btn btn-primary" style="margin:20px" @click="compartilhador()">Compartilhar</button>

      <div v-if="compartilhar">
      <form class="form-items-compartilhados row" @submit.prevent="postarCompartilhamento()">
        <div class="form-group">
          <label >Email do usuário</label>
          <input type="text" class="form-control" placeholder="Entre o email do usuário que receberá o item" v-model="formulario.email">
          <span class="error" v-if="error.email">{{error.email}}</span>
        </div>

        <div class="form-group">
          <div>
            <label for="descricao">Data de início</label>
            <input type="date" v-model="formulario.dataInicio">
            <span class="error" v-if="error.dataInicio">{{error.dataInicio}}</span>
          </div>
          <div>
            <label for="descricao">Data de término</label>
            <input type="date" v-model="formulario.dataTermino">
            <span class="error" v-if="error.dataTermino">{{error.dataTermino}}</span>
          </div>
        </div>

        <button type="submit" class="btn btn-primary">Envia</button>
      </form>
      </div>
      <table class="table table-striped" id="tbItens" style="margin:6px;">
        <thead>
          <tr>
            <th>Id do uso</th>
            <th>Usuário</th>
            <th>Data de Início</th>
            <th>Data de Término</th>
            <th>Status</th>
            <th class="commands"></th>
          </tr>
        </thead>

        <tbody>
          <template>
            <tr v-for="uso in this.compartilhamentos">
              <td>{{uso.id}}</td>
              <td>{{uso.nomeUsuario}}</td>
              <td>{{uso.dataInicio}}</td>
              <td>{{uso.dataTermino}}</td>
              <td>{{uso.status}}</td>
              <template v-if="uso.status === 'Aberto.' || uso.status === 'Aceito.'">
                <button class="btn btn-primary" @click="cancela(uso)">Cancelar compartilhamento</button>
              </template>
            
            </tr>
          </template>
        </tbody>
      </table>
      
      
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
          console.log("Sucesso");
          this.goBackToList();
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
          
        })
        .catch(error => {
          this.error = error.response.data.errors;
        });
    },
    cancela: function(compartilhamento) {
      this.$router.push({ name: 'cancela-compartilhamento', params: {compartilhamento: compartilhamento, lista: false}});
    },
    removerCompartilhamentos: function(compartilhamentoId){
      axios.delete("/api/compartilhamento/" + compartilhamentoId, this.httpOptions)
        .then(response => {
          console.log("Cancelou");
          this.error = {};
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
    width: auto;
    height: 1000px;
    border-radius: 2px;
}

.usos-item{
  margin-top: 20px;
}
</style>