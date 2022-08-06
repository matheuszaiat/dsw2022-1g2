<template lang="html">
  <div class="bloco-descricao">
    <div class="item-descricao"></div>
    <h1>Descrição do Item</h1>
    <ul>
      <li>Nome: {{ item.nome }}</li>
      <li>Descrição: {{ item.descricao }}</li>
      <li>Tipo do item: {{ item.tipo }}</li>
      <div class="usos-item">
          <li>data de início:</li>
          <li>data de término:</li>
          <li>nome do usuário que receberá o item:</li>
          <li>Status do compartilhamento:</li>
      </div>
    </ul>
    <button @click="compartilhador()">Compartilhar</button>
    
    <div v-show="this.compartilhar">
      <form>
        <div class="form-group">
          <label >Email do usuário</label>
          <input type="text" class="form-control" placeholder="Entre o email do usuário que receberá o item">
          <span class="error" ></span>
        </div>

        <div class="form-group">
          <label for="descricao">Data de início</label>
          <textarea rows="1" cols="80" class="form-control"/>
          <label for="descricao">Data de término</label>
          <textarea rows="1" cols="80" class="form-control"/>
        </div>

        <button type="submit" class="btn btn-primary">Envia</button>
      </form>
    </div>
     
  </div>

  
</template>
<script>
  import axios from 'axios';
  
  export default {
    props: ['item'],
    
    data() {
      return {
        compartilhar: false
      }
    },


  methods: {

    goBackToList: function() {
      this.$router.replace('/item/list');
    },
    compartilhador: function() {
      this.compartilhar = !this.compartilhar;
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
    height: 500px;
    background-color: gray;
    border-radius: 2px;
}
</style>